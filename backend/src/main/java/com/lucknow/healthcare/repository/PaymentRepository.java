package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Payment;
import com.lucknow.healthcare.enums.PaymentMethod;
import com.lucknow.healthcare.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payment entity
 * 
 * Provides data access methods for payment management including
 * booking-based queries, status filtering, and transaction tracking.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    /**
     * Find payment by booking
     * 
     * @param booking the booking to filter by
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByBooking(Booking booking);
    
    /**
     * Find payment by booking ID
     * 
     * @param bookingId the booking ID to filter by
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByBookingId(UUID bookingId);
    
    /**
     * Find payments by status
     * 
     * @param status the payment status to filter by
     * @return List of payments with the specified status
     */
    List<Payment> findByStatus(PaymentStatus status);
    
    /**
     * Find payments by method
     * 
     * @param method the payment method to filter by
     * @return List of payments with the specified method
     */
    List<Payment> findByMethod(PaymentMethod method);
    
    /**
     * Find payments by gateway
     * 
     * @param paymentGateway the payment gateway to filter by
     * @return List of payments with the specified gateway
     */
    List<Payment> findByPaymentGateway(String paymentGateway);
    
    /**
     * Find payment by transaction ID
     * 
     * @param transactionId the transaction ID to search for
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payments by status and method
     * 
     * @param status the payment status to filter by
     * @param method the payment method to filter by
     * @return List of payments with the specified status and method
     */
    List<Payment> findByStatusAndMethod(PaymentStatus status, PaymentMethod method);
    
    /**
     * Find payments by amount range
     * 
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @return List of payments within the amount range
     */
    @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * Find payments by status and amount range
     * 
     * @param status the payment status to filter by
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @return List of payments with the specified status within the amount range
     */
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.amount BETWEEN :minAmount AND :maxAmount")
    List<Payment> findByStatusAndAmountRange(@Param("status") PaymentStatus status, 
                                           @Param("minAmount") BigDecimal minAmount, 
                                           @Param("maxAmount") BigDecimal maxAmount);
    
    /**
     * Find payments created within a date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of payments created within the date range
     */
    List<Payment> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find payments by paid date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of payments paid within the date range
     */
    List<Payment> findByPaidAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find successful payments
     * 
     * @return List of successful payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PAID'")
    List<Payment> findSuccessfulPayments();
    
    /**
     * Find failed payments
     * 
     * @return List of failed payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED'")
    List<Payment> findFailedPayments();
    
    /**
     * Find refunded payments
     * 
     * @return List of refunded payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status IN ('REFUNDED', 'PARTIAL_REFUND')")
    List<Payment> findRefundedPayments();
    
    /**
     * Find pending payments
     * 
     * @return List of pending payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING'")
    List<Payment> findPendingPayments();
    
    /**
     * Find payments by booking and status
     * 
     * @param booking the booking to filter by
     * @param status the payment status to filter by
     * @return List of payments for the specified booking and status
     */
    List<Payment> findByBookingAndStatus(Booking booking, PaymentStatus status);
    
    /**
     * Find payments by booking ID and status
     * 
     * @param bookingId the booking ID to filter by
     * @param status the payment status to filter by
     * @return List of payments for the specified booking and status
     */
    List<Payment> findByBookingIdAndStatus(UUID bookingId, PaymentStatus status);
    
    /**
     * Count payments by status
     * 
     * @param status the payment status to count
     * @return number of payments with the specified status
     */
    long countByStatus(PaymentStatus status);
    
    /**
     * Count payments by method
     * 
     * @param method the payment method to count
     * @return number of payments with the specified method
     */
    long countByMethod(PaymentMethod method);
    
    /**
     * Count payments by gateway
     * 
     * @param paymentGateway the payment gateway to count
     * @return number of payments with the specified gateway
     */
    long countByPaymentGateway(String paymentGateway);
    
    /**
     * Calculate total amount by status
     * 
     * @param status the payment status to sum
     * @return total amount of payments with the specified status
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentStatus status);
    
    /**
     * Calculate total amount by method
     * 
     * @param method the payment method to sum
     * @return total amount of payments with the specified method
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.method = :method")
    BigDecimal sumAmountByMethod(@Param("method") PaymentMethod method);
    
    /**
     * Calculate total amount by date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return total amount of payments within the date range
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.createdAt BETWEEN :startDateTime AND :endDateTime")
    BigDecimal sumAmountByDateRange(@Param("startDateTime") LocalDateTime startDateTime, 
                                   @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * Check if payment exists for booking
     * 
     * @param bookingId the booking ID to check
     * @return true if payment exists for the booking, false otherwise
     */
    boolean existsByBookingId(UUID bookingId);
    
    /**
     * Check if transaction ID exists
     * 
     * @param transactionId the transaction ID to check
     * @return true if transaction ID exists, false otherwise
     */
    boolean existsByTransactionId(String transactionId);
    
    // Pageable methods
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    Page<Payment> findByMethod(PaymentMethod method, Pageable pageable);
    
    // Additional query methods
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal calculateTotalAmountByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.method = :method")
    BigDecimal calculateTotalAmountByMethod(@Param("method") PaymentMethod method);
}
