package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.ConsentRecord;
import com.lucknow.healthcare.service.interfaces.ConsentService;
import com.lucknow.healthcare.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Consent management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/consents")
@CrossOrigin(origins = "*")
public class ConsentController {
    
    @Autowired
    private ConsentService consentService;
    
    /**
     * Accept a consent
     */
    @PostMapping("/accept")
    public ResponseEntity<?> acceptConsent(
            @RequestBody ConsentAcceptRequest request,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            ConsentRecord consent;
            
            if (request.getPatientId() != null) {
                // Patient-specific consent
                consent = consentService.acceptPatientConsent(
                    userId, 
                    request.getPatientId(),
                    request.getConsentType(),
                    request.getConsentVersion() != null ? request.getConsentVersion() : 
                        consentService.getLatestConsentVersion(request.getConsentType()),
                    ipAddress,
                    userAgent
                );
            } else {
                // User-level consent
                consent = consentService.acceptConsent(
                    userId,
                    request.getConsentType(),
                    request.getConsentVersion() != null ? request.getConsentVersion() : 
                        consentService.getLatestConsentVersion(request.getConsentType()),
                    ipAddress,
                    userAgent
                );
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(consent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error recording consent: " + e.getMessage());
        }
    }
    
    /**
     * Get required consents for current user
     */
    @GetMapping("/required")
    public ResponseEntity<?> getRequiredConsents() {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            List<ConsentService.RequiredConsent> required = consentService.getRequiredConsents(userId);
            return ResponseEntity.ok(required);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching required consents: " + e.getMessage());
        }
    }
    
    /**
     * Get all consents for current user
     */
    @GetMapping
    public ResponseEntity<?> getUserConsents() {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            List<ConsentRecord> consents = consentService.getUserConsents(userId);
            return ResponseEntity.ok(consents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching consents: " + e.getMessage());
        }
    }
    
    /**
     * Get valid consents for current user
     */
    @GetMapping("/valid")
    public ResponseEntity<?> getValidConsents() {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            List<ConsentRecord> consents = consentService.getValidConsents(userId);
            return ResponseEntity.ok(consents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching valid consents: " + e.getMessage());
        }
    }
    
    /**
     * Validate if user has specific consent
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateConsent(@RequestParam String type) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            ConsentRecord.ConsentType consentType = ConsentRecord.ConsentType.valueOf(type);
            boolean hasConsent = consentService.hasValidConsent(userId, consentType);
            
            return ResponseEntity.ok(new ValidationResponse(hasConsent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid consent type: " + type);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validating consent: " + e.getMessage());
        }
    }
    
    /**
     * Revoke a consent
     */
    @PostMapping("/{consentId}/revoke")
    public ResponseEntity<?> revokeConsent(
            @PathVariable UUID consentId,
            @RequestBody RevokeRequest request) {
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            ConsentRecord revoked = consentService.revokeConsent(consentId, userId, request.getReason());
            return ResponseEntity.ok(revoked);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error revoking consent: " + e.getMessage());
        }
    }
    
    // Helper method to get client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    // Request/Response classes
    
    static class ConsentAcceptRequest {
        private ConsentRecord.ConsentType consentType;
        private String consentVersion;
        private UUID patientId; // Optional - for patient-specific consents
        
        public ConsentRecord.ConsentType getConsentType() { return consentType; }
        public void setConsentType(ConsentRecord.ConsentType consentType) { this.consentType = consentType; }
        
        public String getConsentVersion() { return consentVersion; }
        public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }
        
        public UUID getPatientId() { return patientId; }
        public void setPatientId(UUID patientId) { this.patientId = patientId; }
    }
    
    static class RevokeRequest {
        private String reason;
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    static class ValidationResponse {
        private boolean hasValidConsent;
        
        public ValidationResponse(boolean hasValidConsent) {
            this.hasValidConsent = hasValidConsent;
        }
        
        public boolean isHasValidConsent() { return hasValidConsent; }
        public void setHasValidConsent(boolean hasValidConsent) { this.hasValidConsent = hasValidConsent; }
    }
}

