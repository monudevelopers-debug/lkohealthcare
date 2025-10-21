package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.PatientMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PatientMedication entity
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface PatientMedicationRepository extends JpaRepository<PatientMedication, UUID> {
    
    /**
     * Find all medications for a patient
     */
    List<PatientMedication> findByPatientId(UUID patientId);
    
    /**
     * Find ongoing medications for a patient
     */
    List<PatientMedication> findByPatientIdAndIsOngoingTrue(UUID patientId);
    
    /**
     * Find medication by ID and patient ID (security check)
     */
    Optional<PatientMedication> findByIdAndPatientId(UUID id, UUID patientId);
    
    /**
     * Count ongoing medications for a patient
     */
    long countByPatientIdAndIsOngoingTrue(UUID patientId);
    
    /**
     * Check if medication exists for patient
     */
    boolean existsByIdAndPatientId(UUID id, UUID patientId);
    
    /**
     * Find medications by patient and customer (double security check)
     */
    @Query("SELECT m FROM PatientMedication m WHERE m.id = :medicationId AND " +
           "m.patient.id = :patientId AND m.patient.customer.id = :customerId")
    Optional<PatientMedication> findByIdAndPatientIdAndCustomerId(
        @Param("medicationId") UUID medicationId,
        @Param("patientId") UUID patientId,
        @Param("customerId") UUID customerId
    );
}

