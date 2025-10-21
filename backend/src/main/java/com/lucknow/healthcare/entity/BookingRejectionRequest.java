package com.lucknow.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucknow.healthcare.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BookingRejectionRequest entity representing provider booking rejection requests
 * 
 * This entity stores requests from providers to reject bookings,
 * requiring admin approval for customer protection and reassignment.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "booking_rejection_requests", indexes = {
    @Index(name = "idx_brr_booking", columnList = "booking_id"),
    @Index(name = "idx_brr_provider", columnList = "provider_id"),
    @Index(name = "idx_brr_status", columnList = "status"),
    @Index(name = "idx_brr_requested_at", columnList = "requested_at")
})
@EntityListeners(AuditingEntityListener.class)
public class BookingRejectionRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Booking is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnoreProperties({"reviews", "payments"})
    private Booking booking;
    
    @NotNull(message = "Provider is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    @JsonIgnoreProperties({"services", "bookings", "reviews"})
    private Provider provider;
    
    @NotBlank(message = "Rejection reason is required")
    @Column(name = "rejection_reason", columnDefinition = "TEXT", nullable = false)
    private String rejectionReason;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;
    
    @NotNull(message = "Requested at is required")
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnoreProperties({"password", "emailVerificationToken", "passwordResetToken"})
    private User reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public BookingRejectionRequest() {
        this.requestedAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }
    
    public BookingRejectionRequest(Booking booking, Provider provider, String rejectionReason) {
        this();
        this.booking = booking;
        this.provider = provider;
        this.rejectionReason = rejectionReason;
    }
    
    // Business methods
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }
    
    public boolean isApproved() {
        return status == RequestStatus.APPROVED;
    }
    
    public boolean isRejected() {
        return status == RequestStatus.REJECTED;
    }
    
    public void approve(User admin, String adminNotes) {
        this.status = RequestStatus.APPROVED;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
        this.adminNotes = adminNotes;
    }
    
    public void reject(User admin, String adminNotes) {
        this.status = RequestStatus.REJECTED;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
        this.adminNotes = adminNotes;
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
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public User getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
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

