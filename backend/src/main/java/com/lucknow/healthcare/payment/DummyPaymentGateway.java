package com.lucknow.healthcare.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dummy Payment Gateway for development and testing
 * 
 * Simulates payment processing without actual money transfer.
 * Configurable success rate and processing delay.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
public class DummyPaymentGateway implements PaymentGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(DummyPaymentGateway.class);
    
    @Value("${payment.gateway.dummy.success-rate:90}")
    private int successRate;
    
    @Value("${payment.gateway.dummy.processing-time:2000}")
    private long processingTime;
    
    // In-memory storage for testing
    private final Map<String, PaymentTransaction> transactions = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    @Override
    public PaymentInitiationResponse initiatePayment(PaymentRequest request) {
        logger.info("Initiating dummy payment for booking: {}, amount: {}", 
                   request.getBookingId(), request.getAmount());
        
        // Generate dummy transaction ID
        String transactionId = "DUMMY_TXN_" + UUID.randomUUID().toString()
                                                  .substring(0, 12)
                                                  .toUpperCase();
        String orderId = "DUMMY_ORD_" + UUID.randomUUID().toString()
                                            .substring(0, 12)
                                            .toUpperCase();
        
        // Simulate processing delay
        simulateProcessingDelay();
        
        // Randomly succeed or fail based on success rate
        // Use card number for deterministic testing
        boolean isSuccess = determinePaymentSuccess(request.getPaymentMethod());
        
        // Create transaction record
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setOrderId(orderId);
        transaction.setBookingId(request.getBookingId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(isSuccess ? "SUCCESS" : "FAILED");
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setMessage(isSuccess ? "Payment processed successfully" : 
                                          "Payment failed - Insufficient funds (Test)");
        
        transactions.put(transactionId, transaction);
        
        // Create response
        PaymentInitiationResponse response = new PaymentInitiationResponse();
        response.setTransactionId(transactionId);
        response.setOrderId(orderId);
        response.setStatus(isSuccess ? "SUCCESS" : "FAILED");
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setMessage(transaction.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        logger.info("Dummy payment {} for transaction: {}", 
                   isSuccess ? "SUCCESS" : "FAILED", transactionId);
        
        return response;
    }
    
    @Override
    public PaymentVerificationResponse verifyPayment(String transactionId) {
        logger.info("Verifying payment: {}", transactionId);
        
        PaymentTransaction transaction = transactions.get(transactionId);
        
        PaymentVerificationResponse response = new PaymentVerificationResponse();
        response.setTransactionId(transactionId);
        
        if (transaction == null) {
            response.setStatus("NOT_FOUND");
            response.setVerified(false);
            response.setMessage("Transaction not found");
            return response;
        }
        
        response.setOrderId(transaction.getOrderId());
        response.setStatus(transaction.getStatus());
        response.setAmount(transaction.getAmount());
        response.setPaymentMethod(transaction.getPaymentMethod());
        response.setVerified(transaction.getStatus().equals("SUCCESS"));
        response.setMessage("Payment verification " + (transaction.getStatus().equals("SUCCESS") ? "successful" : "failed"));
        response.setTimestamp(transaction.getTimestamp());
        
        return response;
    }
    
    @Override
    public RefundResponse processRefund(String transactionId, BigDecimal amount, String reason) {
        logger.info("Processing refund for transaction: {}, amount: {}", transactionId, amount);
        
        PaymentTransaction transaction = transactions.get(transactionId);
        
        RefundResponse response = new RefundResponse();
        
        if (transaction == null || !transaction.getStatus().equals("SUCCESS")) {
            response.setSuccess(false);
            response.setMessage("Cannot refund - Transaction not found or not successful");
            return response;
        }
        
        if (amount.compareTo(transaction.getAmount()) > 0) {
            response.setSuccess(false);
            response.setMessage("Refund amount cannot exceed original payment amount");
            return response;
        }
        
        // Simulate refund processing
        simulateProcessingDelay(1000); // Shorter delay for refunds
        
        String refundId = "DUMMY_REFUND_" + UUID.randomUUID().toString()
                                                .substring(0, 12)
                                                .toUpperCase();
        
        // Update transaction status
        if (amount.compareTo(transaction.getAmount()) == 0) {
            transaction.setStatus("REFUNDED");
        } else {
            transaction.setStatus("PARTIALLY_REFUNDED");
        }
        
        response.setSuccess(true);
        response.setRefundId(refundId);
        response.setAmount(amount);
        response.setStatus("PROCESSED");
        response.setMessage("Refund processed successfully (Test Mode)");
        response.setProcessedAt(LocalDateTime.now());
        
        logger.info("Refund processed successfully: {}", refundId);
        
        return response;
    }
    
    @Override
    public PaymentStatus getPaymentStatus(String transactionId) {
        PaymentTransaction transaction = transactions.get(transactionId);
        
        if (transaction == null) {
            return PaymentStatus.FAILED;
        }
        
        return switch (transaction.getStatus()) {
            case "SUCCESS" -> PaymentStatus.SUCCESS;
            case "FAILED" -> PaymentStatus.FAILED;
            case "PENDING" -> PaymentStatus.PENDING;
            case "PROCESSING" -> PaymentStatus.PROCESSING;
            case "REFUNDED" -> PaymentStatus.REFUNDED;
            case "PARTIALLY_REFUNDED" -> PaymentStatus.PARTIALLY_REFUNDED;
            default -> PaymentStatus.FAILED;
        };
    }
    
    // Helper methods
    
    private boolean determinePaymentSuccess(String paymentMethod) {
        // Check for test card numbers (deterministic)
        if (paymentMethod != null) {
            if (paymentMethod.contains("4111111111111111")) {
                return true; // Success card
            } else if (paymentMethod.contains("4000000000000002")) {
                return false; // Failure card
            }
        }
        
        // Random success based on configured rate
        return random.nextInt(100) < successRate;
    }
    
    private void simulateProcessingDelay() {
        simulateProcessingDelay(processingTime);
    }
    
    private void simulateProcessingDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Processing delay interrupted", e);
        }
    }
    
    // Transaction storage class
    
    private static class PaymentTransaction {
        private String transactionId;
        private String orderId;
        private UUID bookingId;
        private UUID customerId;
        private BigDecimal amount;
        private String status;
        private String paymentMethod;
        private String message;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public UUID getBookingId() { return bookingId; }
        public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
        
        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}

