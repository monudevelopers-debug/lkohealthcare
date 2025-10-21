package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.BookingRejectionRequest;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BookingRejectionRequest entity
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface BookingRejectionRequestRepository extends JpaRepository<BookingRejectionRequest, UUID> {
    
    /**
     * Find all requests by status
     */
    List<BookingRejectionRequest> findByStatusOrderByRequestedAtDesc(RequestStatus status);
    
    /**
     * Find pending request for a booking
     */
    Optional<BookingRejectionRequest> findByBookingIdAndStatus(UUID bookingId, RequestStatus status);
    
    /**
     * Find all requests by provider
     */
    @Query("SELECT r FROM BookingRejectionRequest r WHERE r.provider.id = :providerId ORDER BY r.requestedAt DESC")
    List<BookingRejectionRequest> findByProviderId(@Param("providerId") UUID providerId);
    
    /**
     * Find pending requests by provider
     */
    @Query("SELECT r FROM BookingRejectionRequest r WHERE r.provider.id = :providerId AND r.status = :status ORDER BY r.requestedAt DESC")
    List<BookingRejectionRequest> findByProviderIdAndStatus(@Param("providerId") UUID providerId, @Param("status") RequestStatus status);
    
    /**
     * Count pending requests
     */
    long countByStatus(RequestStatus status);
    
    /**
     * Count pending requests by provider
     */
    @Query("SELECT COUNT(r) FROM BookingRejectionRequest r WHERE r.provider.id = :providerId AND r.status = :status")
    long countByProviderIdAndStatus(@Param("providerId") UUID providerId, @Param("status") RequestStatus status);
    
    /**
     * Find all pending requests (shortcut)
     */
    default List<BookingRejectionRequest> findAllPending() {
        return findByStatusOrderByRequestedAtDesc(RequestStatus.PENDING);
    }
}

