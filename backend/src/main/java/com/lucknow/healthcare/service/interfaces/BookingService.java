package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Booking entity operations
 * 
 * Defines business logic methods for booking management including
 * CRUD operations, status updates, provider assignment, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface BookingService {
    
    /**
     * Create a new booking
     * 
     * @param booking the booking to create
     * @return the created booking
     * @throws IllegalArgumentException if user, service, or scheduling conflicts exist
     */
    Booking createBooking(Booking booking);
    
    /**
     * Find booking by ID
     * 
     * @param id the booking ID
     * @return Optional containing the booking if found
     */
    Optional<Booking> findById(UUID id);
    
    /**
     * Update booking
     * 
     * @param booking the booking with updated information
     * @return the updated booking
     * @throws IllegalArgumentException if booking not found
     */
    Booking updateBooking(Booking booking);
    
    /**
     * Update booking status
     * 
     * @param id the booking ID
     * @param status the new booking status
     * @return the updated booking
     * @throws IllegalArgumentException if booking not found or status change is invalid
     */
    Booking updateBookingStatus(UUID id, BookingStatus status);
    
    /**
     * Update booking payment status
     * 
     * @param id the booking ID
     * @param paymentStatus the new payment status
     * @return the updated booking
     * @throws IllegalArgumentException if booking not found
     */
    Booking updateBookingPaymentStatus(UUID id, PaymentStatus paymentStatus);
    
    /**
     * Assign provider to booking
     * 
     * @param id the booking ID
     * @param provider the provider to assign
     * @return the updated booking
     * @throws IllegalArgumentException if booking not found or provider is not available
     */
    Booking assignProvider(UUID id, Provider provider);
    
    /**
     * Cancel booking
     * 
     * @param id the booking ID
     * @return the cancelled booking
     * @throws IllegalArgumentException if booking not found or cannot be cancelled
     */
    Booking cancelBooking(UUID id);
    
    /**
     * Reschedule booking
     * 
     * @param id the booking ID
     * @param newDate the new scheduled date
     * @param newTime the new scheduled time
     * @return the rescheduled booking
     * @throws IllegalArgumentException if booking not found or cannot be rescheduled
     */
    Booking rescheduleBooking(UUID id, LocalDate newDate, LocalTime newTime);
    
    /**
     * Get bookings by user
     * 
     * @param user the user
     * @return List of bookings for the specified user
     */
    List<Booking> getBookingsByUser(User user);
    
    /**
     * Get bookings by user ID
     * 
     * @param userId the user ID
     * @return List of bookings for the specified user
     */
    List<Booking> getBookingsByUserId(UUID userId);
    
    /**
     * Get bookings by provider
     * 
     * @param provider the provider
     * @return List of bookings for the specified provider
     */
    List<Booking> getBookingsByProvider(Provider provider);
    
    /**
     * Get bookings by provider ID
     * 
     * @param providerId the provider ID
     * @return List of bookings for the specified provider
     */
    List<Booking> getBookingsByProviderId(UUID providerId);
    
    /**
     * Get bookings by service
     * 
     * @param service the service
     * @return List of bookings for the specified service
     */
    List<Booking> getBookingsByService(Service service);
    
    /**
     * Get bookings by service ID
     * 
     * @param serviceId the service ID
     * @return List of bookings for the specified service
     */
    List<Booking> getBookingsByServiceId(UUID serviceId);
    
    /**
     * Get bookings by status
     * 
     * @param status the booking status
     * @return List of bookings with the specified status
     */
    List<Booking> getBookingsByStatus(BookingStatus status);
    
    /**
     * Get bookings by payment status
     * 
     * @param paymentStatus the payment status
     * @return List of bookings with the specified payment status
     */
    List<Booking> getBookingsByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Get bookings by user and status
     * 
     * @param user the user
     * @param status the booking status
     * @return List of bookings for the specified user and status
     */
    List<Booking> getBookingsByUserAndStatus(User user, BookingStatus status);
    
    /**
     * Get bookings by user ID and status
     * 
     * @param userId the user ID
     * @param status the booking status
     * @return List of bookings for the specified user and status
     */
    List<Booking> getBookingsByUserIdAndStatus(UUID userId, BookingStatus status);
    
    /**
     * Get bookings by provider and status
     * 
     * @param provider the provider
     * @param status the booking status
     * @return List of bookings for the specified provider and status
     */
    List<Booking> getBookingsByProviderAndStatus(Provider provider, BookingStatus status);
    
    /**
     * Get bookings by provider ID and status
     * 
     * @param providerId the provider ID
     * @param status the booking status
     * @return List of bookings for the specified provider and status
     */
    List<Booking> getBookingsByProviderIdAndStatus(UUID providerId, BookingStatus status);
    
    /**
     * Get bookings by scheduled date
     * 
     * @param scheduledDate the scheduled date
     * @return List of bookings for the specified date
     */
    List<Booking> getBookingsByScheduledDate(LocalDate scheduledDate);
    
    /**
     * Get bookings by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return List of bookings within the date range
     */
    List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get bookings by user and date range
     * 
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @return List of bookings for the specified user within the date range
     */
    List<Booking> getBookingsByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get bookings by provider and date range
     * 
     * @param provider the provider
     * @param startDate the start date
     * @param endDate the end date
     * @return List of bookings for the specified provider within the date range
     */
    List<Booking> getBookingsByProviderAndDateRange(Provider provider, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get upcoming bookings for a user
     * 
     * @param user the user
     * @return List of upcoming bookings for the specified user
     */
    List<Booking> getUpcomingBookingsByUser(User user);
    
    /**
     * Get upcoming bookings for a provider
     * 
     * @param provider the provider
     * @return List of upcoming bookings for the specified provider
     */
    List<Booking> getUpcomingBookingsByProvider(Provider provider);
    
    /**
     * Get bookings created within a date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of bookings created within the date range
     */
    List<Booking> getBookingsByCreatedDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Get bookings that need provider assignment
     * 
     * @return List of bookings without assigned providers
     */
    List<Booking> getBookingsNeedingProviderAssignment();
    
    /**
     * Get all bookings with pagination
     * 
     * @param pageable pagination information
     * @return Page of bookings
     */
    Page<Booking> getAllBookings(Pageable pageable);
    
    /**
     * Get bookings by user with pagination
     * 
     * @param user the user
     * @param pageable pagination information
     * @return Page of bookings for the specified user
     */
    Page<Booking> getBookingsByUser(User user, Pageable pageable);
    
    /**
     * Get bookings by user ID with pagination
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of bookings for the specified user
     */
    Page<Booking> getBookingsByUserId(UUID userId, Pageable pageable);
    
    /**
     * Get bookings by provider with pagination
     * 
     * @param provider the provider
     * @param pageable pagination information
     * @return Page of bookings for the specified provider
     */
    Page<Booking> getBookingsByProvider(Provider provider, Pageable pageable);
    
    /**
     * Get bookings by provider ID with pagination
     * 
     * @param providerId the provider ID
     * @param pageable pagination information
     * @return Page of bookings for the specified provider
     */
    Page<Booking> getBookingsByProviderId(UUID providerId, Pageable pageable);
    
    /**
     * Get bookings by status with pagination
     * 
     * @param status the booking status
     * @param pageable pagination information
     * @return Page of bookings with the specified status
     */
    Page<Booking> getBookingsByStatus(BookingStatus status, Pageable pageable);
    
    /**
     * Count bookings by status
     * 
     * @param status the booking status
     * @return number of bookings with the specified status
     */
    long countBookingsByStatus(BookingStatus status);
    
    /**
     * Count bookings by user
     * 
     * @param user the user
     * @return number of bookings for the specified user
     */
    long countBookingsByUser(User user);
    
    /**
     * Count bookings by provider
     * 
     * @param provider the provider
     * @return number of bookings for the specified provider
     */
    long countBookingsByProvider(Provider provider);
    
    /**
     * Count bookings by service
     * 
     * @param service the service
     * @return number of bookings for the specified service
     */
    long countBookingsByService(Service service);
    
    /**
     * Count bookings by scheduled date
     * 
     * @param scheduledDate the scheduled date
     * @return number of bookings for the specified date
     */
    long countBookingsByScheduledDate(LocalDate scheduledDate);
    
    /**
     * Calculate refund amount for a booking
     * 
     * @param id the booking ID
     * @return the refund amount
     * @throws IllegalArgumentException if booking not found
     */
    BigDecimal calculateRefundAmount(UUID id);
    
    /**
     * Delete booking (soft delete by setting status to CANCELLED)
     * 
     * @param id the booking ID
     * @return true if booking deleted successfully
     * @throws IllegalArgumentException if booking not found
     */
    boolean deleteBooking(UUID id);
    
    /**
     * Count all bookings
     * 
     * @return the total number of bookings
     */
    long countAllBookings();
    
    /**
     * Count active bookings
     * 
     * @return the number of active bookings
     */
    long countActiveBookings();
    
    /**
     * Calculate total revenue
     * 
     * @return the total revenue from all bookings
     */
    double calculateTotalRevenue();
    
    /**
     * Accept a booking (provider accepts the booking request)
     * 
     * @param id the booking ID
     * @return the updated booking with CONFIRMED status
     * @throws IllegalArgumentException if booking not found or not in PENDING status
     */
    Booking acceptBooking(UUID id);
    
    /**
     * Reject a booking (provider rejects the booking request)
     * 
     * @param id the booking ID
     * @param reason optional reason for rejection
     * @return the updated booking with CANCELLED status
     * @throws IllegalArgumentException if booking not found or not in PENDING status
     */
    Booking rejectBooking(UUID id, String reason);
    
    /**
     * Start service delivery (provider marks service as started)
     * 
     * @param id the booking ID
     * @return the updated booking with IN_PROGRESS status
     * @throws IllegalArgumentException if booking not found or not in CONFIRMED status
     */
    Booking startService(UUID id);
    
    /**
     * Complete service delivery (provider marks service as completed)
     * 
     * @param id the booking ID
     * @param notes optional service completion notes
     * @return the updated booking with COMPLETED status
     * @throws IllegalArgumentException if booking not found or not in IN_PROGRESS status
     */
    Booking completeService(UUID id, String notes);
}
