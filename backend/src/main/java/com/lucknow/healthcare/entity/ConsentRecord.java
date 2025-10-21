package com.lucknow.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ConsentRecord entity for tracking user consents
 * 
 * Implements HIPAA-like compliance by tracking when users accept
 * terms and conditions, privacy policies, and medical data sharing agreements.
 * Supports consent versioning and revocation for GDPR compliance.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "consent_records", indexes = {
    @Index(name = "idx_consent_records_user_id", columnList = "user_id"),
    @Index(name = "idx_consent_records_patient_id", columnList = "patient_id"),
    @Index(name = "idx_consent_records_is_accepted", columnList = "is_accepted"),
    @Index(name = "idx_consent_records_consent_type", columnList = "consent_type")
}, uniqueConstraints = {
    @UniqueConstraint(name = "idx_consent_records_unique", 
                     columnNames = {"user_id", "consent_type", "consent_version"}),
    @UniqueConstraint(name = "idx_consent_records_patient_unique", 
                     columnNames = {"user_id", "patient_id", "consent_type", "consent_version"})
})
@EntityListeners(AuditingEntityListener.class)
public class ConsentRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient; // NULL for user-level consents, populated for patient-specific
    
    @NotNull(message = "Consent type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 50)
    private ConsentType consentType;
    
    @NotBlank(message = "Consent version is required")
    @Size(max = 20, message = "Version string too long")
    @Column(name = "consent_version", nullable = false, length = 20)
    private String consentVersion;
    
    @NotNull(message = "Acceptance status is required")
    @Column(name = "is_accepted", nullable = false)
    private Boolean isAccepted = false;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Size(max = 45, message = "IP address too long")
    @Column(name = "ip_address", length = 45)
    private String ipAddress; // Support IPv4 and IPv6
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent; // Browser/device information for audit
    
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    
    @Column(name = "revocation_reason", columnDefinition = "TEXT")
    private String revocationReason;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Enums
    
    public enum ConsentType {
        TERMS_AND_CONDITIONS,    // General T&C for platform use
        PRIVACY_POLICY,          // Privacy policy and data handling
        MEDICAL_DATA_SHARING,    // Consent to share medical data with providers
        HIPAA_COMPLIANCE,        // HIPAA-like compliance for healthcare data
        EMERGENCY_TREATMENT,     // Consent for emergency treatment
        DATA_RETENTION           // Consent for data retention and storage
    }
    
    // Constructors
    
    public ConsentRecord() {}
    
    public ConsentRecord(User user, ConsentType consentType, String consentVersion) {
        this.user = user;
        this.consentType = consentType;
        this.consentVersion = consentVersion;
    }
    
    public ConsentRecord(User user, Patient patient, ConsentType consentType, String consentVersion) {
        this.user = user;
        this.patient = patient;
        this.consentType = consentType;
        this.consentVersion = consentVersion;
    }
    
    // Business methods
    
    public void accept(String ipAddress, String userAgent) {
        this.isAccepted = true;
        this.acceptedAt = LocalDateTime.now();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    public void revoke(String reason) {
        this.isAccepted = false;
        this.revokedAt = LocalDateTime.now();
        this.revocationReason = reason;
    }
    
    public boolean isValid() {
        return isAccepted != null && isAccepted && revokedAt == null;
    }
    
    public boolean isPatientSpecific() {
        return patient != null;
    }
    
    public boolean requiresReacceptance() {
        // If revoked, requires re-acceptance
        return revokedAt != null;
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
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public ConsentType getConsentType() {
        return consentType;
    }
    
    public void setConsentType(ConsentType consentType) {
        this.consentType = consentType;
    }
    
    public String getConsentVersion() {
        return consentVersion;
    }
    
    public void setConsentVersion(String consentVersion) {
        this.consentVersion = consentVersion;
    }
    
    public Boolean getIsAccepted() {
        return isAccepted;
    }
    
    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }
    
    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }
    
    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }
    
    public String getRevocationReason() {
        return revocationReason;
    }
    
    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

