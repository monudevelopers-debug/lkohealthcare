package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.PatientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PatientDocument entity
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface PatientDocumentRepository extends JpaRepository<PatientDocument, UUID> {
    
    /**
     * Find all documents for a patient
     */
    List<PatientDocument> findByPatientIdOrderByUploadedAtDesc(UUID patientId);
    
    /**
     * Find documents by type for a patient
     */
    List<PatientDocument> findByPatientIdAndDocumentTypeOrderByUploadedAtDesc(
        UUID patientId, 
        PatientDocument.DocumentType documentType
    );
    
    /**
     * Find document by ID and patient ID (security check)
     */
    Optional<PatientDocument> findByIdAndPatientId(UUID id, UUID patientId);
    
    /**
     * Find verified documents for a patient
     */
    List<PatientDocument> findByPatientIdAndIsVerifiedTrue(UUID patientId);
    
    /**
     * Count documents for a patient
     */
    long countByPatientId(UUID patientId);
    
    /**
     * Check if document exists for patient
     */
    boolean existsByIdAndPatientId(UUID id, UUID patientId);
    
    /**
     * Find documents by patient and customer (double security check)
     */
    @Query("SELECT d FROM PatientDocument d WHERE d.id = :documentId AND " +
           "d.patient.id = :patientId AND d.patient.customer.id = :customerId")
    Optional<PatientDocument> findByIdAndPatientIdAndCustomerId(
        @Param("documentId") UUID documentId,
        @Param("patientId") UUID patientId,
        @Param("customerId") UUID customerId
    );
    
    /**
     * Find all documents uploaded by a user
     */
    List<PatientDocument> findByUploadedById(UUID userId);
}

