package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Payment entity operations (Phase 1.5)
 * 
 * Defines business logic methods for payment management including
 * payment processing, refunds, and transaction tracking.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface PaymentService {
    
    /**
     * Create a new payment
     * 
     * @param payment the payment to create
     * @return the created payment
     * @throws IllegalArgumentException if booking not found or payment already exists
     */
    Payment createPayment(Payment payment);
    
    /**
     * Find payment by ID
     * 
     * @param id the payment ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findById(UUID id);
    
    /**
     * Find payment by booking ID
     * 
     * @param bookingId the booking ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByBookingId(UUID bookingId);
    
    /**
     * Update payment
     * 
     * @param payment the payment with updated information
     * @return the updated payment
     * @throws IllegalArgumentException if payment not found
     */
    Payment updatePayment(Payment payment);
    
    /**
     * Update payment status
     * 
     * @param id the payment ID
     * @param status the new payment status
     * @return the updated payment
     * @throws IllegalArgumentException if payment not found
     */
    Payment updatePaymentStatus(UUID id, Payment.PaymentStatus status);
    
    /**
     * Process payment
     * 
     * @param id the payment ID
     * @param transactionId the transaction ID from payment gateway
     * @param gatewayResponse the gateway response data
     * @return the updated payment
     * @throws IllegalArgumentException if payment not found or already processed
     */
    Payment processPayment(UUID id, String transactionId, String gatewayResponse);
    
    /**
     * Process refund
     * 
     * @param id the payment ID
     * @param refundAmount the refund amount
     * @return the updated payment
     * @throws IllegalArgumentException if payment not found or refund amount is invalid
     */
    Payment processRefund(UUID id, BigDecimal refundAmount);
    
    /**
     * Get payments by status
     * 
     * @param status the payment status
     * @return List of payments with the specified status
     */
    List<Payment> getPaymentsByStatus(Payment.PaymentStatus status);
    
    /**
     * Get payments by method
     * 
     * @param method the payment method
     * @return List of payments with the specified method
     */
    List<Payment> getPaymentsByMethod(String method);
    
    /**
     * Get payments by date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of payments created within the date range
     */
    List<Payment> getPaymentsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Get payments by transaction ID
     * 
     * @param transactionId the transaction ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> getPaymentByTransactionId(String transactionId);
    
    /**
     * Get all payments with pagination
     * 
     * @param pageable pagination information
     * @return Page of payments
     */
    Page<Payment> getAllPayments(Pageable pageable);
    
    /**
     * Get payments by status with pagination
     * 
     * @param status the payment status
     * @param pageable pagination information
     * @return Page of payments with the specified status
     */
    Page<Payment> getPaymentsByStatus(Payment.PaymentStatus status, Pageable pageable);
    
    /**
     * Get payments by method with pagination
     * 
     * @param method the payment method
     * @param pageable pagination information
     * @return Page of payments with the specified method
     */
    Page<Payment> getPaymentsByMethod(String method, Pageable pageable);
    
    /**
     * Count payments by status
     * 
     * @param status the payment status
     * @return number of payments with the specified status
     */
    long countPaymentsByStatus(Payment.PaymentStatus status);
    
    /**
     * Count payments by method
     * 
     * @param method the payment method
     * @return number of payments with the specified method
     */
    long countPaymentsByMethod(String method);
    
    /**
     * Calculate total amount by status
     * 
     * @param status the payment status
     * @return total amount of payments with the specified status
     */
    BigDecimal calculateTotalAmountByStatus(Payment.PaymentStatus status);
    
    /**
     * Calculate total amount by method
     * 
     * @param method the payment method
     * @return total amount of payments with the specified method
     */
    BigDecimal calculateTotalAmountByMethod(String method);
    
    /**
     * Generate invoice for payment
     * 
     * @param id the payment ID
     * @return the invoice number
     * @throws IllegalArgumentException if payment not found
     */
    String generateInvoice(UUID id);
    
    /**
     * Delete payment (soft delete by setting status to CANCELLED)
     * 
     * @param id the payment ID
     * @return true if payment deleted successfully
     * @throws IllegalArgumentException if payment not found
     */
    boolean deletePayment(UUID id);
}
