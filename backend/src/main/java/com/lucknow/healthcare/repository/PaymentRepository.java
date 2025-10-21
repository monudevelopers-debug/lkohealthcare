package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payment entity
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payment by invoice number
     */
    Optional<Payment> findByInvoiceNumber(String invoiceNumber);
    
    /**
     * Find payment by booking ID
     */
    Optional<Payment> findByBookingId(UUID bookingId);
    
    /**
     * Find all payments for a customer
     */
    Page<Payment> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
    
    /**
     * Find payments by status for a customer
     */
    Page<Payment> findByCustomerIdAndPaymentStatusOrderByCreatedAtDesc(
        UUID customerId, 
        Payment.PaymentStatus status, 
        Pageable pageable
    );
    
    /**
     * Find successful payments for a customer
     */
    List<Payment> findByCustomerIdAndPaymentStatus(UUID customerId, Payment.PaymentStatus status);
    
    /**
     * Count successful payments for a customer
     */
    long countByCustomerIdAndPaymentStatus(UUID customerId, Payment.PaymentStatus status);
    
    /**
     * Calculate total amount paid by customer
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.customer.id = :customerId AND p.paymentStatus = :status")
    BigDecimal calculateTotalPaidAmount(@Param("customerId") UUID customerId, 
                                        @Param("status") Payment.PaymentStatus status);
    
    /**
     * Find pending payments for a customer
     */
    List<Payment> findByCustomerIdAndPaymentStatusIn(UUID customerId, List<Payment.PaymentStatus> statuses);
    
    /**
     * Check if payment exists for booking
     */
    boolean existsByBookingId(UUID bookingId);
}
