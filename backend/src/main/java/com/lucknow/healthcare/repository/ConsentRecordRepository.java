package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ConsentRecord entity
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, UUID> {
    
    /**
     * Find all consent records for a user
     */
    List<ConsentRecord> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    /**
     * Find valid (accepted and not revoked) consents for a user
     */
    @Query("SELECT c FROM ConsentRecord c WHERE c.user.id = :userId AND " +
           "c.isAccepted = true AND c.revokedAt IS NULL")
    List<ConsentRecord> findValidConsentsByUserId(@Param("userId") UUID userId);
    
    /**
     * Find consent by user, type, and version
     */
    Optional<ConsentRecord> findByUserIdAndConsentTypeAndConsentVersion(
        UUID userId, 
        ConsentRecord.ConsentType consentType, 
        String consentVersion
    );
    
    /**
     * Find consent by user, patient, type, and version
     */
    Optional<ConsentRecord> findByUserIdAndPatientIdAndConsentTypeAndConsentVersion(
        UUID userId,
        UUID patientId,
        ConsentRecord.ConsentType consentType,
        String consentVersion
    );
    
    /**
     * Find all consents for a patient
     */
    List<ConsentRecord> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    
    /**
     * Check if user has accepted a specific consent type (any version)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ConsentRecord c " +
           "WHERE c.user.id = :userId AND c.consentType = :consentType AND " +
           "c.isAccepted = true AND c.revokedAt IS NULL")
    boolean hasValidConsent(@Param("userId") UUID userId, 
                           @Param("consentType") ConsentRecord.ConsentType consentType);
    
    /**
     * Check if user has accepted a specific consent version
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ConsentRecord c " +
           "WHERE c.user.id = :userId AND c.consentType = :consentType AND " +
           "c.consentVersion = :version AND c.isAccepted = true AND c.revokedAt IS NULL")
    boolean hasValidConsentVersion(@Param("userId") UUID userId,
                                   @Param("consentType") ConsentRecord.ConsentType consentType,
                                   @Param("version") String version);
    
    /**
     * Find users who need to re-accept consents due to version update
     */
    @Query("SELECT DISTINCT c.user.id FROM ConsentRecord c " +
           "WHERE c.consentType = :consentType AND c.consentVersion != :latestVersion AND " +
           "c.isAccepted = true AND c.revokedAt IS NULL")
    List<UUID> findUsersRequiringConsentUpdate(
        @Param("consentType") ConsentRecord.ConsentType consentType,
        @Param("latestVersion") String latestVersion
    );
}

