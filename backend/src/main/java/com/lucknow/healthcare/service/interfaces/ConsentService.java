package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.ConsentRecord;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Consent management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface ConsentService {
    
    /**
     * Record user consent acceptance
     */
    ConsentRecord acceptConsent(UUID userId, ConsentRecord.ConsentType consentType, 
                               String consentVersion, String ipAddress, String userAgent);
    
    /**
     * Record patient-specific consent
     */
    ConsentRecord acceptPatientConsent(UUID userId, UUID patientId, 
                                      ConsentRecord.ConsentType consentType,
                                      String consentVersion, String ipAddress, String userAgent);
    
    /**
     * Revoke a consent
     */
    ConsentRecord revokeConsent(UUID consentId, UUID userId, String reason);
    
    /**
     * Get all consents for a user
     */
    List<ConsentRecord> getUserConsents(UUID userId);
    
    /**
     * Get valid consents for a user
     */
    List<ConsentRecord> getValidConsents(UUID userId);
    
    /**
     * Get required consents that user needs to accept
     */
    List<RequiredConsent> getRequiredConsents(UUID userId);
    
    /**
     * Check if user has valid consent for a specific type
     */
    boolean hasValidConsent(UUID userId, ConsentRecord.ConsentType consentType);
    
    /**
     * Check if user has accepted a specific consent version
     */
    boolean hasValidConsentVersion(UUID userId, ConsentRecord.ConsentType consentType, String version);
    
    /**
     * Get latest consent version for a type
     */
    String getLatestConsentVersion(ConsentRecord.ConsentType consentType);
    
    /**
     * DTO for required consents
     */
    class RequiredConsent {
        private ConsentRecord.ConsentType consentType;
        private String latestVersion;
        private boolean isAccepted;
        private String title;
        private String description;
        
        public RequiredConsent(ConsentRecord.ConsentType consentType, String latestVersion, 
                             boolean isAccepted, String title, String description) {
            this.consentType = consentType;
            this.latestVersion = latestVersion;
            this.isAccepted = isAccepted;
            this.title = title;
            this.description = description;
        }
        
        // Getters
        public ConsentRecord.ConsentType getConsentType() { return consentType; }
        public String getLatestVersion() { return latestVersion; }
        public boolean isAccepted() { return isAccepted; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        
        // Setters
        public void setConsentType(ConsentRecord.ConsentType consentType) { this.consentType = consentType; }
        public void setLatestVersion(String latestVersion) { this.latestVersion = latestVersion; }
        public void setAccepted(boolean accepted) { isAccepted = accepted; }
        public void setTitle(String title) { this.title = title; }
        public void setDescription(String description) { this.description = description; }
    }
}

