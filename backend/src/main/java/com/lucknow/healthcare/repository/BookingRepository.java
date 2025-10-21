package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Booking entity
 * 
 * Provides data access methods for booking management including
 * user bookings, provider assignments, status filtering, and date-based queries.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    /**
     * Find bookings by user
     * 
     * @param user the user to filter by
     * @return List of bookings for the specified user
     */
    List<Booking> findByUser(User user);
    
    /**
     * Find bookings by user ID
     * 
     * @param userId the user ID to filter by
     * @return List of bookings for the specified user
     */
    List<Booking> findByUserId(UUID userId);
    
    /**
     * Find bookings by provider
     * 
     * @param provider the provider to filter by
     * @return List of bookings for the specified provider
     */
    List<Booking> findByProvider(Provider provider);
    
    /**
     * Find bookings by provider ID
     * 
     * @param providerId the provider ID to filter by
     * @return List of bookings for the specified provider
     */
    List<Booking> findByProviderId(UUID providerId);
    
    /**
     * Find bookings by service
     * 
     * @param service the service to filter by
     * @return List of bookings for the specified service
     */
    List<Booking> findByService(Service service);
    
    /**
     * Find bookings by service ID
     * 
     * @param serviceId the service ID to filter by
     * @return List of bookings for the specified service
     */
    List<Booking> findByServiceId(UUID serviceId);
    
    /**
     * Find bookings by status
     * 
     * @param status the booking status to filter by
     * @return List of bookings with the specified status
     */
    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Find bookings by payment status
     * 
     * @param paymentStatus the payment status to filter by
     * @return List of bookings with the specified payment status
     */
    List<Booking> findByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Find bookings by user and status
     * 
     * @param user the user to filter by
     * @param status the booking status to filter by
     * @return List of bookings for the specified user and status
     */
    List<Booking> findByUserAndStatus(User user, BookingStatus status);
    
    /**
     * Find bookings by user ID and status
     * 
     * @param userId the user ID to filter by
     * @param status the booking status to filter by
     * @return List of bookings for the specified user and status
     */
    List<Booking> findByUserIdAndStatus(UUID userId, BookingStatus status);
    
    /**
     * Find bookings by provider and status
     * 
     * @param provider the provider to filter by
     * @param status the booking status to filter by
     * @return List of bookings for the specified provider and status
     */
    List<Booking> findByProviderAndStatus(Provider provider, BookingStatus status);
    
    /**
     * Find bookings by provider and multiple statuses
     * 
     * @param provider the provider to filter by
     * @param statuses the list of booking statuses to filter by
     * @return List of bookings for the specified provider and statuses
     */
    List<Booking> findByProviderAndStatusIn(Provider provider, List<BookingStatus> statuses);
    
    /**
     * Find bookings by provider ID and status
     * 
     * @param providerId the provider ID to filter by
     * @param status the booking status to filter by
     * @return List of bookings for the specified provider and status
     */
    List<Booking> findByProviderIdAndStatus(UUID providerId, BookingStatus status);
    
    /**
     * Find bookings by scheduled date
     * 
     * @param scheduledDate the scheduled date to filter by
     * @return List of bookings for the specified date
     */
    List<Booking> findByScheduledDate(LocalDate scheduledDate);
    
    /**
     * Find bookings by date range
     * 
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return List of bookings within the date range
     */
    List<Booking> findByScheduledDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find bookings by user and date range
     * 
     * @param user the user to filter by
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return List of bookings for the specified user within the date range
     */
    List<Booking> findByUserAndScheduledDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find bookings by provider and date range
     * 
     * @param provider the provider to filter by
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return List of bookings for the specified provider within the date range
     */
    List<Booking> findByProviderAndScheduledDateBetween(Provider provider, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find upcoming bookings for a user
     * 
     * @param user the user to filter by
     * @param currentDate the current date
     * @return List of upcoming bookings for the specified user
     */
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.scheduledDate >= :currentDate ORDER BY b.scheduledDate, b.scheduledTime")
    List<Booking> findUpcomingBookingsByUser(@Param("user") User user, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Find upcoming bookings for a provider
     * 
     * @param provider the provider to filter by
     * @param currentDate the current date
     * @return List of upcoming bookings for the specified provider
     */
    @Query("SELECT b FROM Booking b WHERE b.provider = :provider AND b.scheduledDate >= :currentDate ORDER BY b.scheduledDate, b.scheduledTime")
    List<Booking> findUpcomingBookingsByProvider(@Param("provider") Provider provider, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Find bookings created within a date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of bookings created within the date range
     */
    List<Booking> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Count bookings by status
     * 
     * @param status the booking status to count
     * @return number of bookings with the specified status
     */
    long countByStatus(BookingStatus status);
    
    /**
     * Count bookings by multiple statuses
     * 
     * @param statuses the list of booking statuses to count
     * @return number of bookings with any of the specified statuses
     */
    long countByStatusIn(List<BookingStatus> statuses);
    
    /**
     * Count bookings by user
     * 
     * @param user the user to count bookings for
     * @return number of bookings for the specified user
     */
    long countByUser(User user);
    
    /**
     * Count bookings by provider
     * 
     * @param provider the provider to count bookings for
     * @return number of bookings for the specified provider
     */
    long countByProvider(Provider provider);
    
    /**
     * Count bookings by service
     * 
     * @param service the service to count bookings for
     * @return number of bookings for the specified service
     */
    long countByService(Service service);
    
    /**
     * Count bookings by scheduled date
     * 
     * @param scheduledDate the scheduled date to count
     * @return number of bookings for the specified date
     */
    long countByScheduledDate(LocalDate scheduledDate);
    
    /**
     * Find bookings that need provider assignment
     * 
     * @return List of bookings without assigned providers
     */
    @Query("SELECT b FROM Booking b WHERE b.provider IS NULL AND b.status = 'PENDING'")
    List<Booking> findBookingsNeedingProviderAssignment();
    
    /**
     * Find bookings that need provider assignment or can be reassigned
     * (not cancelled or completed)
     */
    @Query("SELECT b FROM Booking b WHERE (b.provider IS NULL OR b.status IN ('PENDING', 'CONFIRMED')) AND b.status NOT IN ('CANCELLED', 'COMPLETED') ORDER BY b.scheduledDate ASC, b.scheduledTime ASC")
    List<Booking> findUnassignedBookings();
    
    /**
     * Find unassigned bookings (no provider assigned) with pagination
     */
    @Query("SELECT b FROM Booking b WHERE b.provider IS NULL AND b.status != 'CANCELLED' ORDER BY b.scheduledDate ASC, b.scheduledTime ASC")
    Page<Booking> findUnassignedBookings(Pageable pageable);
    
    // Pageable methods
    Page<Booking> findByUser(User user, Pageable pageable);
    Page<Booking> findByUserId(UUID userId, Pageable pageable);
    Page<Booking> findByProvider(Provider provider, Pageable pageable);
    Page<Booking> findByProviderId(UUID providerId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
}
