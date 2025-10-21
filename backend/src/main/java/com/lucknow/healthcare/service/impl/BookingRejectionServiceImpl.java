package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.BookingRejectionRequest;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.RequestStatus;
import com.lucknow.healthcare.repository.BookingRepository;
import com.lucknow.healthcare.repository.BookingRejectionRequestRepository;
import com.lucknow.healthcare.repository.ProviderRepository;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.service.interfaces.BookingRejectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for BookingRejectionRequest operations
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@org.springframework.stereotype.Service
@Transactional
public class BookingRejectionServiceImpl implements BookingRejectionService {
    
    @Autowired
    private BookingRejectionRequestRepository rejectionRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public BookingRejectionRequest requestBookingRejection(UUID bookingId, UUID providerId, String reason) {
        // Validate booking
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        
        // Validate provider
        Provider provider = providerRepository.findById(providerId)
            .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));
        
        // Check if booking belongs to this provider
        if (booking.getProvider() == null || !booking.getProvider().getId().equals(providerId)) {
            throw new IllegalArgumentException("This booking is not assigned to you");
        }
        
        // Check if booking can be rejected
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reject completed or cancelled bookings");
        }
        
        // Check for existing pending request
        Optional<BookingRejectionRequest> existing = rejectionRepository.findByBookingIdAndStatus(
            bookingId, RequestStatus.PENDING
        );
        
        if (existing.isPresent()) {
            throw new IllegalStateException("A pending rejection request already exists for this booking");
        }
        
        // Create rejection request
        BookingRejectionRequest request = new BookingRejectionRequest();
        request.setBooking(booking);
        request.setProvider(provider);
        request.setRejectionReason(reason);
        request.setStatus(RequestStatus.PENDING);
        
        return rejectionRepository.save(request);
    }
    
    @Override
    public BookingRejectionRequest approveRejection(UUID requestId, UUID adminId, String adminNotes) {
        // Find request
        BookingRejectionRequest request = rejectionRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Rejection request not found with ID: " + requestId));
        
        // Find admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));
        
        // Check if pending
        if (!request.isPending()) {
            throw new IllegalStateException("Only pending requests can be approved. Current status: " + request.getStatus());
        }
        
        // Approve the rejection
        request.approve(admin, adminNotes);
        
        // Cancel the booking and unassign provider
        Booking booking = request.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setProvider(null); // Unassign so admin can reassign
        booking.setNotes("Provider rejected - Reason: " + request.getRejectionReason());
        
        bookingRepository.save(booking);
        
        return rejectionRepository.save(request);
    }
    
    @Override
    public BookingRejectionRequest denyRejection(UUID requestId, UUID adminId, String adminNotes) {
        // Find request
        BookingRejectionRequest request = rejectionRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Rejection request not found with ID: " + requestId));
        
        // Find admin
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));
        
        // Check if pending
        if (!request.isPending()) {
            throw new IllegalStateException("Only pending requests can be denied. Current status: " + request.getStatus());
        }
        
        // Deny the rejection (provider must complete the booking)
        request.reject(admin, adminNotes);
        
        return rejectionRepository.save(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingRejectionRequest> getAllPendingRequests() {
        return rejectionRepository.findAllPending();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingRejectionRequest> getRequestsByProvider(UUID providerId) {
        return rejectionRepository.findByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BookingRejectionRequest> getPendingRequestByBooking(UUID bookingId) {
        return rejectionRepository.findByBookingIdAndStatus(bookingId, RequestStatus.PENDING);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BookingRejectionRequest> findById(UUID requestId) {
        return rejectionRepository.findById(requestId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return rejectionRepository.countByStatus(RequestStatus.PENDING);
    }
}

