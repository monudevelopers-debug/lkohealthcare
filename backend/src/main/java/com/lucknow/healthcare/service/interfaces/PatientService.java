package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Patient management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface PatientService {
    
    /**
     * Create a new patient
     */
    Patient createPatient(Patient patient, UUID customerId);
    
    /**
     * Update an existing patient
     */
    Patient updatePatient(UUID patientId, Patient patient, UUID customerId);
    
    /**
     * Get patient by ID (with customer verification)
     */
    Optional<Patient> getPatientById(UUID patientId, UUID customerId);
    
    /**
     * Get all active patients for a customer
     */
    List<Patient> getActivePatients(UUID customerId);
    
    /**
     * Get all patients (including inactive) for a customer with pagination
     */
    Page<Patient> getAllPatients(UUID customerId, Pageable pageable);
    
    /**
     * Soft delete a patient (mark as inactive)
     */
    boolean deletePatient(UUID patientId, UUID customerId);
    
    /**
     * Search patients by name
     */
    Page<Patient> searchPatients(UUID customerId, String name, Pageable pageable);
    
    /**
     * Get patients with medical conditions
     */
    List<Patient> getPatientsWithMedicalConditions(UUID customerId);
    
    /**
     * Get minor patients
     */
    List<Patient> getMinorPatients(UUID customerId);
    
    /**
     * Count active patients for a customer
     */
    long countActivePatients(UUID customerId);
    
    /**
     * Check if patient belongs to customer
     */
    boolean verifyPatientOwnership(UUID patientId, UUID customerId);
}

