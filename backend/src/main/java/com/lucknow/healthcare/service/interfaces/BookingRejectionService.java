package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.BookingRejectionRequest;
import com.lucknow.healthcare.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for BookingRejectionRequest operations
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface BookingRejectionService {
    
    /**
     * Provider requests to reject a booking
     */
    BookingRejectionRequest requestBookingRejection(UUID bookingId, UUID providerId, String reason);
    
    /**
     * Admin approves rejection request
     */
    BookingRejectionRequest approveRejection(UUID requestId, UUID adminId, String adminNotes);
    
    /**
     * Admin denies rejection request (provider must complete booking)
     */
    BookingRejectionRequest denyRejection(UUID requestId, UUID adminId, String adminNotes);
    
    /**
     * Get all pending rejection requests
     */
    List<BookingRejectionRequest> getAllPendingRequests();
    
    /**
     * Get rejection requests by provider
     */
    List<BookingRejectionRequest> getRequestsByProvider(UUID providerId);
    
    /**
     * Get pending rejection request for a booking
     */
    Optional<BookingRejectionRequest> getPendingRequestByBooking(UUID bookingId);
    
    /**
     * Find request by ID
     */
    Optional<BookingRejectionRequest> findById(UUID requestId);
    
    /**
     * Count pending requests
     */
    long countPendingRequests();
}

