package com.lucknow.healthcare.entity;

import com.lucknow.healthcare.enums.PaymentMethod;
import com.lucknow.healthcare.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment entity representing payment records (Phase 1.5)
 * 
 * This entity stores payment information including booking reference,
 * amount, method, status, and gateway transaction details.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_booking_id", columnList = "booking_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_payment_gateway", columnList = "payment_gateway"),
    @Index(name = "idx_payment_created_at", columnList = "created_at"),
    @Index(name = "idx_payment_paid_at", columnList = "paid_at")
})
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Size(max = 255, message = "Transaction ID must not exceed 255 characters")
    @Column(length = 255)
    private String transactionId;
    
    @Size(max = 50, message = "Payment gateway must not exceed 50 characters")
    @Column(length = 50)
    private String paymentGateway;
    
    @Column(columnDefinition = "JSONB")
    private String gatewayResponse; // Store gateway response data as JSON
    
    private LocalDateTime paidAt;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Payment() {}
    
    public Payment(Booking booking, BigDecimal amount, PaymentMethod method) {
        this.booking = booking;
        this.amount = amount;
        this.method = method;
    }
    
    // Business methods
    public boolean processRefund(BigDecimal refundAmount) {
        if (status == PaymentStatus.PAID && refundAmount.compareTo(BigDecimal.ZERO) > 0 && 
            refundAmount.compareTo(amount) <= 0) {
            if (refundAmount.equals(amount)) {
                status = PaymentStatus.REFUNDED;
            } else {
                status = PaymentStatus.PARTIAL_REFUND;
            }
            return true;
        }
        return false;
    }
    
    public String generateInvoice() {
        // Generate invoice number based on booking ID and timestamp
        return String.format("INV-%s-%d", 
            booking.getId().toString().substring(0, 8).toUpperCase(),
            System.currentTimeMillis() % 10000);
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentGateway() {
        return paymentGateway;
    }
    
    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
    
    public String getGatewayResponse() {
        return gatewayResponse;
    }
    
    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
    
    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
