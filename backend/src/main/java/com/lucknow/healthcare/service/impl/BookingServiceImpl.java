package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;
import com.lucknow.healthcare.repository.BookingRepository;
import com.lucknow.healthcare.service.interfaces.BookingService;
import com.lucknow.healthcare.service.interfaces.UserService;
import com.lucknow.healthcare.service.interfaces.ServiceService;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Booking entity operations
 * 
 * Implements business logic for booking management including
 * CRUD operations, status updates, provider assignment, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@org.springframework.stereotype.Service
@Transactional
public class BookingServiceImpl implements BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private com.lucknow.healthcare.repository.ProviderRepository providerRepository;
    
    @Override
    public Booking createBooking(Booking booking) {
        // Validate user exists
        if (booking.getUser() == null || booking.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required for booking");
        }
        
        Optional<User> userOpt = userService.findById(booking.getUser().getId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + booking.getUser().getId());
        }
        
        // Validate service exists
        if (booking.getService() == null || booking.getService().getId() == null) {
            throw new IllegalArgumentException("Service is required for booking");
        }
        
        Optional<Service> serviceOpt = serviceService.findById(booking.getService().getId());
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found with ID: " + booking.getService().getId());
        }
        
        // Validate scheduled date is in the future
        if (booking.getScheduledDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Scheduled date must be in the future");
        }
        
        // Set default values
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        
        return bookingRepository.save(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> findById(UUID id) {
        return bookingRepository.findById(id);
    }
    
    @Override
    public Booking updateBooking(Booking booking) {
        if (!bookingRepository.existsById(booking.getId())) {
            throw new IllegalArgumentException("Booking not found with ID: " + booking.getId());
        }
        
        return bookingRepository.save(booking);
    }
    
    @Override
    public Booking updateBookingStatus(UUID id, BookingStatus status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        BookingStatus oldStatus = booking.getStatus();
        
        // BUSINESS RULE: Cannot start service without a provider assigned
        if (status == BookingStatus.IN_PROGRESS && booking.getProvider() == null) {
            throw new IllegalStateException(
                "Cannot start service - No provider assigned to this booking. " +
                "Please assign a provider first."
            );
        }
        
        booking.setStatus(status);
        
        // AUTO-UPDATE PROVIDER STATUS based on booking status
        if (booking.getProvider() != null) {
            Provider provider = booking.getProvider();
            
            // When service starts (IN_PROGRESS), set provider to BUSY
            if (status == BookingStatus.IN_PROGRESS) {
                provider.setAvailabilityStatus(com.lucknow.healthcare.enums.AvailabilityStatus.BUSY);
                providerRepository.save(provider);
                System.out.println("Provider " + provider.getName() + " auto-set to BUSY (booking started)");
            }
            
            // When service completes or cancels, check if provider should return to AVAILABLE
            if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
                // Check if provider has other IN_PROGRESS bookings
                List<Booking> otherInProgressBookings = bookingRepository.findByProviderAndStatus(
                    provider, 
                    BookingStatus.IN_PROGRESS
                );
                
                // If no other active bookings, set back to AVAILABLE
                if (otherInProgressBookings.isEmpty()) {
                    provider.setAvailabilityStatus(com.lucknow.healthcare.enums.AvailabilityStatus.AVAILABLE);
                    providerRepository.save(provider);
                    System.out.println("Provider " + provider.getName() + " auto-set to AVAILABLE (all bookings completed)");
                }
            }
        }
        
        return bookingRepository.save(booking);
    }
    
    @Override
    public Booking updateBookingPaymentStatus(UUID id, PaymentStatus paymentStatus) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        booking.setPaymentStatus(paymentStatus);
        
        return bookingRepository.save(booking);
    }
    
    @Override
    public Booking assignProvider(UUID id, Provider provider) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        // Validate provider exists and is available
        if (provider == null || provider.getId() == null) {
            throw new IllegalArgumentException("Provider is required");
        }
        
        Optional<Provider> providerOpt = providerService.findById(provider.getId());
        if (providerOpt.isEmpty()) {
            throw new IllegalArgumentException("Provider not found with ID: " + provider.getId());
        }
        
        Provider actualProvider = providerOpt.get();
        Booking booking = bookingOpt.get();
        
        // BUSINESS RULE 1: Provider must be AVAILABLE (not BUSY, OFF_DUTY, or ON_LEAVE)
        if (actualProvider.getAvailabilityStatus() != com.lucknow.healthcare.enums.AvailabilityStatus.AVAILABLE) {
            throw new IllegalStateException(
                "Provider " + actualProvider.getName() + " is currently " + 
                actualProvider.getAvailabilityStatus() + ". Only AVAILABLE providers can be assigned."
            );
        }
        
        // BUSINESS RULE 2: Check for time conflicts with existing bookings
        LocalDate bookingDate = booking.getScheduledDate();
        LocalTime bookingStartTime = booking.getScheduledTime();
        LocalTime bookingEndTime = bookingStartTime.plusHours(booking.getDuration());
        
        // Get all CONFIRMED or IN_PROGRESS bookings for this provider on the same date
        List<Booking> providerBookings = bookingRepository.findByProviderAndStatusIn(
            actualProvider,
            List.of(BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS)
        ).stream()
        .filter(b -> b.getScheduledDate().equals(bookingDate))
        .toList();
        
        // Check for time overlap
        for (Booking existingBooking : providerBookings) {
            LocalTime existingStart = existingBooking.getScheduledTime();
            LocalTime existingEnd = existingStart.plusHours(existingBooking.getDuration());
            
            // Check if times overlap
            boolean overlap = !(bookingEndTime.isBefore(existingStart) || bookingStartTime.isAfter(existingEnd));
            
            if (overlap) {
                throw new IllegalStateException(
                    "Time conflict! Provider " + actualProvider.getName() + 
                    " already has a booking from " + existingStart + " to " + existingEnd + 
                    " on " + bookingDate
                );
            }
        }
        
        // All validations passed - assign provider
        booking.setProvider(actualProvider);
        
        System.out.println("Provider " + actualProvider.getName() + " assigned to booking " + booking.getId());
        
        return bookingRepository.save(booking);
    }
    
    @Override
    public Booking cancelBooking(UUID id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.canBeCancelled()) {
            throw new IllegalArgumentException("Booking cannot be cancelled in current status: " + booking.getStatus());
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        
        return bookingRepository.save(booking);
    }
    
    @Override
    public Booking rescheduleBooking(UUID id, LocalDate newDate, LocalTime newTime) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        
        if (!booking.canBeRescheduled()) {
            throw new IllegalArgumentException("Booking cannot be rescheduled in current status: " + booking.getStatus());
        }
        
        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("New scheduled date must be in the future");
        }
        
        booking.setScheduledDate(newDate);
        booking.setScheduledTime(newTime);
        
        return bookingRepository.save(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserId(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProvider(Provider provider) {
        return bookingRepository.findByProviderWithPatient(provider);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProviderId(UUID providerId) {
        return bookingRepository.findByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByService(Service service) {
        return bookingRepository.findByService(service);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByServiceId(UUID serviceId) {
        return bookingRepository.findByServiceId(serviceId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByPaymentStatus(PaymentStatus paymentStatus) {
        return bookingRepository.findByPaymentStatus(paymentStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserAndStatus(User user, BookingStatus status) {
        return bookingRepository.findByUserAndStatus(user, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserIdAndStatus(UUID userId, BookingStatus status) {
        return bookingRepository.findByUserIdAndStatus(userId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProviderAndStatus(Provider provider, BookingStatus status) {
        return bookingRepository.findByProviderAndStatus(provider, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProviderIdAndStatus(UUID providerId, BookingStatus status) {
        return bookingRepository.findByProviderIdAndStatus(providerId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByScheduledDate(LocalDate scheduledDate) {
        return bookingRepository.findByScheduledDate(scheduledDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByScheduledDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByUserAndScheduledDateBetween(user, startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProviderAndDateRange(Provider provider, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByProviderAndScheduledDateBetween(provider, startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUpcomingBookingsByUser(User user) {
        return bookingRepository.findUpcomingBookingsByUser(user, LocalDate.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUpcomingBookingsByProvider(Provider provider) {
        return bookingRepository.findUpcomingBookingsByProvider(provider, LocalDate.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCreatedDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return bookingRepository.findByCreatedAtBetween(startDateTime, endDateTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsNeedingProviderAssignment() {
        return bookingRepository.findBookingsNeedingProviderAssignment();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByUser(User user, Pageable pageable) {
        return bookingRepository.findByUser(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByUserId(UUID userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByProvider(Provider provider, Pageable pageable) {
        return bookingRepository.findByProviderWithPatient(provider, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByProviderId(UUID providerId, Pageable pageable) {
        return bookingRepository.findByProviderId(providerId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Booking> getBookingsByStatus(BookingStatus status, Pageable pageable) {
        return bookingRepository.findByStatus(status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countBookingsByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countBookingsByUser(User user) {
        return bookingRepository.countByUser(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countBookingsByProvider(Provider provider) {
        return bookingRepository.countByProvider(provider);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countBookingsByService(Service service) {
        return bookingRepository.countByService(service);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countBookingsByScheduledDate(LocalDate scheduledDate) {
        return bookingRepository.countByScheduledDate(scheduledDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateRefundAmount(UUID id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        return bookingOpt.get().calculateRefundAmount();
    }
    
    @Override
    public boolean deleteBooking(UUID id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        booking.setStatus(BookingStatus.CANCELLED);
        
        bookingRepository.save(booking);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Booking> findUnassignedBookings() {
        return bookingRepository.findUnassignedBookings();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAllBookings() {
        return bookingRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveBookings() {
        return bookingRepository.countByStatusIn(List.of(
            BookingStatus.PENDING, 
            BookingStatus.CONFIRMED, 
            BookingStatus.IN_PROGRESS
        ));
    }
    
    @Override
    @Transactional(readOnly = true)
    public double calculateTotalRevenue() {
        List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);
        return completedBookings.stream()
            .mapToDouble(booking -> booking.getTotalAmount() != null ? booking.getTotalAmount().doubleValue() : 0.0)
            .sum();
    }
    
    @Override
    @Transactional
    public Booking acceptBooking(UUID id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be accepted. Current status: " + booking.getStatus());
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
    
    @Override
    @Transactional
    public Booking rejectBooking(UUID id, String reason) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING bookings can be rejected. Current status: " + booking.getStatus());
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        if (reason != null && !reason.isEmpty()) {
            String currentNotes = booking.getNotes() != null ? booking.getNotes() + "\n" : "";
            booking.setNotes(currentNotes + "Rejection reason: " + reason);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
    
    @Override
    @Transactional
    public Booking startService(UUID id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Only CONFIRMED bookings can be started. Current status: " + booking.getStatus());
        }
        
        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
    
    @Override
    @Transactional
    public Booking completeService(UUID id, String notes) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + id);
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Only IN_PROGRESS bookings can be completed. Current status: " + booking.getStatus());
        }
        
        booking.setStatus(BookingStatus.COMPLETED);
        if (notes != null && !notes.isEmpty()) {
            String currentNotes = booking.getNotes() != null ? booking.getNotes() + "\n" : "";
            booking.setNotes(currentNotes + "Completion notes: " + notes);
        }
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }
}
