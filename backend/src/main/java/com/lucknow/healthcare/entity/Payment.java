package com.lucknow.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment entity representing payment transactions
 * 
 * Tracks all payment operations including online payments, cash payments,
 * refunds, and invoice generation.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_booking_id", columnList = "booking_id"),
    @Index(name = "idx_payments_customer_id", columnList = "customer_id"),
    @Index(name = "idx_payments_payment_status", columnList = "payment_status"),
    @Index(name = "idx_payments_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_payments_invoice_number", columnList = "invoice_number"),
    @Index(name = "idx_payments_created_at", columnList = "created_at"),
    @Index(name = "idx_payments_customer_status", columnList = "customer_id, payment_status")
})
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Booking is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // CARD, UPI, CASH, WALLET, ONLINE
    
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway; // DUMMY, RAZORPAY, STRIPE
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "gateway_order_id")
    private String gatewayOrderId; // Razorpay order_id, Stripe payment_intent
    
    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @NotNull(message = "Payment timing is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_timing", nullable = false, length = 20)
    private PaymentTiming paymentTiming;
    
    @Column(name = "invoice_number", unique = true, length = 50)
    private String invoiceNumber;
    
    @Column(name = "invoice_url", columnDefinition = "TEXT")
    private String invoiceUrl;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;
    
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse; // Store full JSON response for debugging
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    
    public enum PaymentStatus {
        PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }
    
    public enum PaymentTiming {
        ADVANCE, POST_SERVICE
    }
    
    // Constructors
    
    public Payment() {}
    
    public Payment(Booking booking, User customer, BigDecimal amount, 
                  String paymentMethod, PaymentTiming paymentTiming) {
        this.booking = booking;
        this.customer = customer;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentTiming = paymentTiming;
    }
    
    // Business methods
    
    public boolean isSuccessful() {
        return paymentStatus == PaymentStatus.SUCCESS;
    }
    
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING || paymentStatus == PaymentStatus.PROCESSING;
    }
    
    public boolean canBeRefunded() {
        return paymentStatus == PaymentStatus.SUCCESS || paymentStatus == PaymentStatus.PARTIALLY_REFUNDED;
    }
    
    public void markAsSuccess(String transactionId) {
        this.transactionId = transactionId;
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.paidAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failureReason = reason;
    }
    
    public void generateInvoiceNumber() {
        if (invoiceNumber == null && paymentStatus == PaymentStatus.SUCCESS) {
            this.invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
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
    
    public User getCustomer() {
        return customer;
    }
    
    public void setCustomer(User customer) {
        this.customer = customer;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentGateway() {
        return paymentGateway;
    }
    
    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getGatewayOrderId() {
        return gatewayOrderId;
    }
    
    public void setGatewayOrderId(String gatewayOrderId) {
        this.gatewayOrderId = gatewayOrderId;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public PaymentTiming getPaymentTiming() {
        return paymentTiming;
    }
    
    public void setPaymentTiming(PaymentTiming paymentTiming) {
        this.paymentTiming = paymentTiming;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public String getInvoiceUrl() {
        return invoiceUrl;
    }
    
    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }
    
    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
    
    public String getGatewayResponse() {
        return gatewayResponse;
    }
    
    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
