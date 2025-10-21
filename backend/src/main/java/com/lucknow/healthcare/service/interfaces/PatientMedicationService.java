package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.PatientMedication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for PatientMedication management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface PatientMedicationService {
    
    /**
     * Add medication for a patient
     */
    PatientMedication addMedication(UUID patientId, PatientMedication medication, UUID customerId);
    
    /**
     * Update medication
     */
    PatientMedication updateMedication(UUID medicationId, UUID patientId, PatientMedication medication, UUID customerId);
    
    /**
     * Get medication by ID
     */
    Optional<PatientMedication> getMedicationById(UUID medicationId, UUID patientId, UUID customerId);
    
    /**
     * Get all medications for a patient
     */
    List<PatientMedication> getPatientMedications(UUID patientId, UUID customerId);
    
    /**
     * Get ongoing medications for a patient
     */
    List<PatientMedication> getOngoingMedications(UUID patientId, UUID customerId);
    
    /**
     * Stop a medication
     */
    PatientMedication stopMedication(UUID medicationId, UUID patientId, UUID customerId);
    
    /**
     * Delete medication
     */
    boolean deleteMedication(UUID medicationId, UUID patientId, UUID customerId);
    
    /**
     * Count ongoing medications for a patient
     */
    long countOngoingMedications(UUID patientId, UUID customerId);
}

