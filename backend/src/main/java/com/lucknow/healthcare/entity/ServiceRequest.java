package com.lucknow.healthcare.entity;

import com.lucknow.healthcare.enums.RequestType;
import com.lucknow.healthcare.enums.RequestStatus;
import com.lucknow.healthcare.enums.Requester;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ServiceRequest entity representing provider service addition/removal requests
 * 
 * This entity stores requests from providers to add or remove services,
 * requiring admin approval for safety and compliance.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "provider_service_requests", indexes = {
    @Index(name = "idx_psr_provider", columnList = "provider_id"),
    @Index(name = "idx_psr_service", columnList = "service_id"),
    @Index(name = "idx_psr_status", columnList = "status"),
    @Index(name = "idx_psr_requested_by", columnList = "requested_by"),
    @Index(name = "idx_psr_requested_at", columnList = "requested_at")
})
@EntityListeners(AuditingEntityListener.class)
public class ServiceRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Provider is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
    
    @NotNull(message = "Service is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @NotNull(message = "Request type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 20)
    private RequestType requestType;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;
    
    @NotNull(message = "Requested at is required")
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @NotNull(message = "Requested by is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_by", nullable = false, length = 10)
    private Requester requestedBy;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public ServiceRequest() {
        this.requestedAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
    }
    
    public ServiceRequest(Provider provider, Service service, RequestType requestType, Requester requestedBy) {
        this();
        this.provider = provider;
        this.service = service;
        this.requestType = requestType;
        this.requestedBy = requestedBy;
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
    
    public boolean isFromProvider() {
        return requestedBy == Requester.PROVIDER;
    }
    
    public boolean isFromAdmin() {
        return requestedBy == Requester.ADMIN;
    }
    
    public void approve(User admin) {
        this.status = RequestStatus.APPROVED;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
    }
    
    public void reject(User admin, String reason) {
        this.status = RequestStatus.REJECTED;
        this.reviewedBy = admin;
        this.reviewedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public RequestType getRequestType() {
        return requestType;
    }
    
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
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
    
    public Requester getRequestedBy() {
        return requestedBy;
    }
    
    public void setRequestedBy(Requester requestedBy) {
        this.requestedBy = requestedBy;
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
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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

