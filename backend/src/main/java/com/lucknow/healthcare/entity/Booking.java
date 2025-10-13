package com.lucknow.healthcare.entity;

import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Booking entity representing service bookings
 * 
 * This entity stores booking information including user, service, provider,
 * scheduling details, payment status, and special instructions.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_user_id", columnList = "user_id"),
    @Index(name = "idx_booking_service_id", columnList = "service_id"),
    @Index(name = "idx_booking_provider_id", columnList = "provider_id"),
    @Index(name = "idx_booking_status", columnList = "status"),
    @Index(name = "idx_booking_scheduled_date", columnList = "scheduled_date"),
    @Index(name = "idx_booking_payment_status", columnList = "payment_status"),
    @Index(name = "idx_booking_created_at", columnList = "created_at"),
    @Index(name = "idx_booking_user_status", columnList = "user_id, status"),
    @Index(name = "idx_booking_provider_status", columnList = "provider_id, status"),
    @Index(name = "idx_booking_date_status", columnList = "scheduled_date, status")
})
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Service is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    @Column(nullable = false)
    private LocalDate scheduledDate;
    
    @NotNull(message = "Scheduled time is required")
    @Column(nullable = false)
    private LocalTime scheduledTime;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Column(nullable = false)
    private Integer duration; // Duration in hours
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Size(max = 1000, message = "Special instructions must not exceed 1000 characters")
    @Column(length = 1000)
    private String specialInstructions;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(length = 1000)
    private String notes;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Booking() {}
    
    public Booking(User user, Service service, LocalDate scheduledDate, LocalTime scheduledTime, Integer duration, BigDecimal totalAmount) {
        this.user = user;
        this.service = service;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.duration = duration;
        this.totalAmount = totalAmount;
    }
    
    // Business methods
    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
    
    public boolean canBeRescheduled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
    
    public BigDecimal calculateRefundAmount() {
        if (status == BookingStatus.CANCELLED && paymentStatus == PaymentStatus.PAID) {
            // Full refund if cancelled before service starts
            if (scheduledDate.isAfter(LocalDate.now()) || 
                (scheduledDate.isEqual(LocalDate.now()) && scheduledTime.isAfter(LocalTime.now()))) {
                return totalAmount;
            }
            // Partial refund if cancelled after service starts
            return totalAmount.multiply(BigDecimal.valueOf(0.5));
        }
        return BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public LocalTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
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
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
