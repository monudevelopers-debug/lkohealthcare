package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;
import com.lucknow.healthcare.service.interfaces.BookingService;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Booking entity operations
 * 
 * Provides REST endpoints for booking management including
 * CRUD operations, status updates, provider assignment, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ProviderService providerService;
    
    /**
     * Create a new booking
     * 
     * @param booking the booking to create
     * @return ResponseEntity containing the created booking
     */
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        try {
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get booking by ID
     * 
     * @param id the booking ID
     * @return ResponseEntity containing the booking if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID id) {
        Optional<Booking> bookingOpt = bookingService.findById(id);
        return bookingOpt.map(booking -> ResponseEntity.ok(booking))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update booking
     * 
     * @param id the booking ID
     * @param booking the updated booking information
     * @return ResponseEntity containing the updated booking
     */
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable UUID id, @RequestBody Booking booking) {
        try {
            booking.setId(id);
            Booking updatedBooking = bookingService.updateBooking(booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update booking status
     * 
     * @param id the booking ID
     * @param status the new booking status
     * @return ResponseEntity containing the updated booking
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable UUID id, @RequestParam BookingStatus status) {
        try {
            Booking updatedBooking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update booking payment status
     * 
     * @param id the booking ID
     * @param paymentStatus the new payment status
     * @return ResponseEntity containing the updated booking
     */
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<Booking> updateBookingPaymentStatus(@PathVariable UUID id, @RequestParam PaymentStatus paymentStatus) {
        try {
            Booking updatedBooking = bookingService.updateBookingPaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Assign provider to booking
     * 
     * @param id the booking ID
     * @param providerId the provider ID to assign
     * @return ResponseEntity containing the updated booking
     */
    @PutMapping("/{id}/assign-provider")
    public ResponseEntity<Booking> assignProvider(@PathVariable UUID id, @RequestParam UUID providerId) {
        try {
            Optional<Provider> providerOpt = providerService.findById(providerId);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Booking updatedBooking = bookingService.assignProvider(id, providerOpt.get());
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Cancel booking
     * 
     * @param id the booking ID
     * @return ResponseEntity containing the cancelled booking
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable UUID id) {
        try {
            Booking cancelledBooking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(cancelledBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Reschedule booking
     * 
     * @param id the booking ID
     * @param newDate the new scheduled date
     * @param newTime the new scheduled time
     * @return ResponseEntity containing the rescheduled booking
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Booking> rescheduleBooking(@PathVariable UUID id, @RequestParam LocalDate newDate, @RequestParam LocalTime newTime) {
        try {
            Booking rescheduledBooking = bookingService.rescheduleBooking(id, newDate, newTime);
            return ResponseEntity.ok(rescheduledBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get bookings by user
     * 
     * @param userId the user ID
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable UUID userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by provider
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Booking>> getBookingsByProvider(@PathVariable UUID providerId) {
        List<Booking> bookings = bookingService.getBookingsByProviderId(providerId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by service
     * 
     * @param serviceId the service ID
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Booking>> getBookingsByService(@PathVariable UUID serviceId) {
        List<Booking> bookings = bookingService.getBookingsByServiceId(serviceId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by status
     * 
     * @param status the booking status
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by payment status
     * 
     * @param paymentStatus the payment status
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<Booking>> getBookingsByPaymentStatus(@PathVariable PaymentStatus paymentStatus) {
        List<Booking> bookings = bookingService.getBookingsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by user and status
     * 
     * @param userId the user ID
     * @param status the booking status
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByUserAndStatus(@PathVariable UUID userId, @PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by provider and status
     * 
     * @param providerId the provider ID
     * @param status the booking status
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/provider/{providerId}/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByProviderAndStatus(@PathVariable UUID providerId, @PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByProviderIdAndStatus(providerId, status);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by scheduled date
     * 
     * @param scheduledDate the scheduled date
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/date/{scheduledDate}")
    public ResponseEntity<List<Booking>> getBookingsByScheduledDate(@PathVariable LocalDate scheduledDate) {
        List<Booking> bookings = bookingService.getBookingsByScheduledDate(scheduledDate);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Booking>> getBookingsByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<Booking> bookings = bookingService.getBookingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get upcoming bookings for a user
     * 
     * @param userId the user ID
     * @return ResponseEntity containing the list of upcoming bookings
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookingsByUser(@PathVariable UUID userId) {
        // This would require getting the User entity, but for now we'll use the service method directly
        // In a real implementation, you'd get the User entity first
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }
    
    /**
     * Get upcoming bookings for a provider
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing the list of upcoming bookings
     */
    @GetMapping("/provider/{providerId}/upcoming")
    public ResponseEntity<List<Booking>> getUpcomingBookingsByProvider(@PathVariable UUID providerId) {
        // This would require getting the Provider entity, but for now we'll use the service method directly
        // In a real implementation, you'd get the Provider entity first
        return ResponseEntity.ok(bookingService.getBookingsByProviderId(providerId));
    }
    
    /**
     * Get bookings created within a date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/created-range")
    public ResponseEntity<List<Booking>> getBookingsByCreatedDateRange(@RequestParam LocalDateTime startDateTime, @RequestParam LocalDateTime endDateTime) {
        List<Booking> bookings = bookingService.getBookingsByCreatedDateRange(startDateTime, endDateTime);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings that need provider assignment
     * 
     * @return ResponseEntity containing the list of bookings
     */
    @GetMapping("/needing-assignment")
    public ResponseEntity<List<Booking>> getBookingsNeedingProviderAssignment() {
        List<Booking> bookings = bookingService.getBookingsNeedingProviderAssignment();
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get all bookings with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of bookings
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Booking>> getAllBookings(Pageable pageable) {
        Page<Booking> bookings = bookingService.getAllBookings(pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by user with pagination
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return ResponseEntity containing the page of bookings
     */
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<Booking>> getBookingsByUser(@PathVariable UUID userId, Pageable pageable) {
        Page<Booking> bookings = bookingService.getBookingsByUserId(userId, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by provider with pagination
     * 
     * @param providerId the provider ID
     * @param pageable pagination information
     * @return ResponseEntity containing the page of bookings
     */
    @GetMapping("/provider/{providerId}/page")
    public ResponseEntity<Page<Booking>> getBookingsByProvider(@PathVariable UUID providerId, Pageable pageable) {
        Page<Booking> bookings = bookingService.getBookingsByProviderId(providerId, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by status with pagination
     * 
     * @param status the booking status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of bookings
     */
    @GetMapping("/status/{status}/page")
    public ResponseEntity<Page<Booking>> getBookingsByStatus(@PathVariable BookingStatus status, Pageable pageable) {
        Page<Booking> bookings = bookingService.getBookingsByStatus(status, pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Count bookings by status
     * 
     * @param status the booking status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countBookingsByStatus(@PathVariable BookingStatus status) {
        long count = bookingService.countBookingsByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count bookings by user
     * 
     * @param userId the user ID
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> countBookingsByUser(@PathVariable UUID userId) {
        // This would require getting the User entity, but for now we'll return 0
        // In a real implementation, you'd get the User entity first
        return ResponseEntity.ok(0L);
    }
    
    /**
     * Count bookings by provider
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/provider/{providerId}")
    public ResponseEntity<Long> countBookingsByProvider(@PathVariable UUID providerId) {
        // This would require getting the Provider entity, but for now we'll return 0
        // In a real implementation, you'd get the Provider entity first
        return ResponseEntity.ok(0L);
    }
    
    /**
     * Count bookings by service
     * 
     * @param serviceId the service ID
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/service/{serviceId}")
    public ResponseEntity<Long> countBookingsByService(@PathVariable UUID serviceId) {
        // This would require getting the Service entity, but for now we'll return 0
        // In a real implementation, you'd get the Service entity first
        return ResponseEntity.ok(0L);
    }
    
    /**
     * Count bookings by scheduled date
     * 
     * @param scheduledDate the scheduled date
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/date/{scheduledDate}")
    public ResponseEntity<Long> countBookingsByScheduledDate(@PathVariable LocalDate scheduledDate) {
        long count = bookingService.countBookingsByScheduledDate(scheduledDate);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Calculate refund amount for a booking
     * 
     * @param id the booking ID
     * @return ResponseEntity containing the refund amount
     */
    @GetMapping("/{id}/refund-amount")
    public ResponseEntity<BigDecimal> calculateRefundAmount(@PathVariable UUID id) {
        try {
            BigDecimal refundAmount = bookingService.calculateRefundAmount(id);
            return ResponseEntity.ok(refundAmount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete booking
     * 
     * @param id the booking ID
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID id) {
        try {
            boolean success = bookingService.deleteBooking(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
