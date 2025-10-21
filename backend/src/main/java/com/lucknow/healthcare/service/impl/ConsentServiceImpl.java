package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.ConsentRecord;
import com.lucknow.healthcare.entity.Patient;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.repository.ConsentRecordRepository;
import com.lucknow.healthcare.repository.PatientRepository;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.service.interfaces.ConsentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for Consent management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ConsentServiceImpl implements ConsentService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsentServiceImpl.class);
    
    // Current consent versions
    private static final Map<ConsentRecord.ConsentType, String> CONSENT_VERSIONS = new HashMap<>();
    
    static {
        CONSENT_VERSIONS.put(ConsentRecord.ConsentType.TERMS_AND_CONDITIONS, "1.0");
        CONSENT_VERSIONS.put(ConsentRecord.ConsentType.PRIVACY_POLICY, "1.0");
        CONSENT_VERSIONS.put(ConsentRecord.ConsentType.MEDICAL_DATA_SHARING, "1.0");
        CONSENT_VERSIONS.put(ConsentRecord.ConsentType.HIPAA_COMPLIANCE, "1.0");
        CONSENT_VERSIONS.put(ConsentRecord.ConsentType.EMERGENCY_TREATMENT, "1.0");
        CONSENT_VERSIONS.put(ConsentRecord.ConsentType.DATA_RETENTION, "1.0");
    }
    
    @Autowired
    private ConsentRecordRepository consentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public ConsentRecord acceptConsent(UUID userId, ConsentRecord.ConsentType consentType, 
                                      String consentVersion, String ipAddress, String userAgent) {
        logger.info("Recording consent acceptance for user: {}, type: {}", userId, consentType);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if consent already exists
        var existingConsent = consentRepository
            .findByUserIdAndConsentTypeAndConsentVersion(userId, consentType, consentVersion);
        
        if (existingConsent.isPresent()) {
            ConsentRecord consent = existingConsent.get();
            if (consent.getIsAccepted() && consent.getRevokedAt() == null) {
                logger.info("Consent already accepted for user: {}, type: {}", userId, consentType);
                return consent;
            }
            // Re-accept if previously revoked
            consent.accept(ipAddress, userAgent);
            return consentRepository.save(consent);
        }
        
        // Create new consent record
        ConsentRecord consent = new ConsentRecord(user, consentType, consentVersion);
        consent.accept(ipAddress, userAgent);
        
        ConsentRecord saved = consentRepository.save(consent);
        logger.info("Consent recorded successfully with ID: {}", saved.getId());
        
        return saved;
    }
    
    @Override
    public ConsentRecord acceptPatientConsent(UUID userId, UUID patientId, 
                                             ConsentRecord.ConsentType consentType,
                                             String consentVersion, String ipAddress, String userAgent) {
        logger.info("Recording patient consent for user: {}, patient: {}, type: {}", userId, patientId, consentType);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Patient patient = patientRepository.findByIdAndCustomerId(patientId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found or access denied"));
        
        // Check if consent already exists
        var existingConsent = consentRepository
            .findByUserIdAndPatientIdAndConsentTypeAndConsentVersion(userId, patientId, consentType, consentVersion);
        
        if (existingConsent.isPresent()) {
            ConsentRecord consent = existingConsent.get();
            if (consent.getIsAccepted() && consent.getRevokedAt() == null) {
                logger.info("Patient consent already accepted");
                return consent;
            }
            consent.accept(ipAddress, userAgent);
            return consentRepository.save(consent);
        }
        
        // Create new consent record
        ConsentRecord consent = new ConsentRecord(user, patient, consentType, consentVersion);
        consent.accept(ipAddress, userAgent);
        
        ConsentRecord saved = consentRepository.save(consent);
        logger.info("Patient consent recorded successfully with ID: {}", saved.getId());
        
        return saved;
    }
    
    @Override
    public ConsentRecord revokeConsent(UUID consentId, UUID userId, String reason) {
        logger.info("Revoking consent: {} for user: {}", consentId, userId);
        
        ConsentRecord consent = consentRepository.findById(consentId)
            .orElseThrow(() -> new IllegalArgumentException("Consent not found"));
        
        // Verify ownership
        if (!consent.getUser().getId().equals(userId)) {
            throw new SecurityException("Access denied - consent belongs to different user");
        }
        
        consent.revoke(reason);
        
        ConsentRecord revoked = consentRepository.save(consent);
        logger.info("Consent revoked successfully: {}", consentId);
        
        return revoked;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConsentRecord> getUserConsents(UUID userId) {
        return consentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConsentRecord> getValidConsents(UUID userId) {
        return consentRepository.findValidConsentsByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RequiredConsent> getRequiredConsents(UUID userId) {
        List<RequiredConsent> required = new ArrayList<>();
        
        // Required consents for all users
        addRequiredConsent(required, userId, ConsentRecord.ConsentType.TERMS_AND_CONDITIONS,
            "Terms and Conditions",
            "Agreement to use Lucknow Healthcare platform services");
        
        addRequiredConsent(required, userId, ConsentRecord.ConsentType.PRIVACY_POLICY,
            "Privacy Policy",
            "How we collect, use, and protect your personal information");
        
        addRequiredConsent(required, userId, ConsentRecord.ConsentType.MEDICAL_DATA_SHARING,
            "Medical Data Sharing",
            "Consent to share patient medical information with assigned healthcare providers");
        
        addRequiredConsent(required, userId, ConsentRecord.ConsentType.HIPAA_COMPLIANCE,
            "HIPAA Compliance",
            "Understanding of HIPAA-like protections for your medical data");
        
        return required;
    }
    
    private void addRequiredConsent(List<RequiredConsent> list, UUID userId, 
                                   ConsentRecord.ConsentType type, String title, String description) {
        String latestVersion = getLatestConsentVersion(type);
        boolean isAccepted = hasValidConsentVersion(userId, type, latestVersion);
        
        list.add(new RequiredConsent(type, latestVersion, isAccepted, title, description));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasValidConsent(UUID userId, ConsentRecord.ConsentType consentType) {
        return consentRepository.hasValidConsent(userId, consentType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasValidConsentVersion(UUID userId, ConsentRecord.ConsentType consentType, String version) {
        return consentRepository.hasValidConsentVersion(userId, consentType, version);
    }
    
    @Override
    public String getLatestConsentVersion(ConsentRecord.ConsentType consentType) {
        return CONSENT_VERSIONS.getOrDefault(consentType, "1.0");
    }
}

