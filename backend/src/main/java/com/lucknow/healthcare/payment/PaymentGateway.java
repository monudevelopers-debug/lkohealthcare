package com.lucknow.healthcare.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment Gateway interface
 * 
 * Abstraction for payment processing allowing multiple gateway implementations
 * (Dummy, Razorpay, Stripe, etc.)
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface PaymentGateway {
    
    /**
     * Initiate a payment
     */
    PaymentInitiationResponse initiatePayment(PaymentRequest request);
    
    /**
     * Verify payment status
     */
    PaymentVerificationResponse verifyPayment(String transactionId);
    
    /**
     * Process refund
     */
    RefundResponse processRefund(String transactionId, BigDecimal amount, String reason);
    
    /**
     * Get payment status
     */
    PaymentStatus getPaymentStatus(String transactionId);
    
    // DTOs
    
    class PaymentRequest {
        private UUID bookingId;
        private UUID customerId;
        private BigDecimal amount;
        private String paymentMethod; // CARD, UPI, CASH, etc.
        private String currency = "INR";
        
        // Getters and Setters
        public UUID getBookingId() { return bookingId; }
        public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
        
        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
    
    class PaymentInitiationResponse {
        private String transactionId;
        private String orderId;
        private String status; // SUCCESS, FAILED, PENDING
        private BigDecimal amount;
        private String currency;
        private String message;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    class PaymentVerificationResponse {
        private String transactionId;
        private String orderId;
        private String status;
        private BigDecimal amount;
        private String paymentMethod;
        private boolean isVerified;
        private String message;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public boolean isVerified() { return isVerified; }
        public void setVerified(boolean verified) { isVerified = verified; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    class RefundResponse {
        private boolean success;
        private String refundId;
        private BigDecimal amount;
        private String status;
        private String message;
        private LocalDateTime processedAt;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getRefundId() { return refundId; }
        public void setRefundId(String refundId) { this.refundId = refundId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    }
    
    enum PaymentStatus {
        PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }
}

