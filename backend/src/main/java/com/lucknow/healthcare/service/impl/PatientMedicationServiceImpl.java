package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Patient;
import com.lucknow.healthcare.entity.PatientMedication;
import com.lucknow.healthcare.repository.PatientMedicationRepository;
import com.lucknow.healthcare.repository.PatientRepository;
import com.lucknow.healthcare.service.interfaces.PatientMedicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for PatientMedication management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class PatientMedicationServiceImpl implements PatientMedicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientMedicationServiceImpl.class);
    
    @Autowired
    private PatientMedicationRepository medicationRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public PatientMedication addMedication(UUID patientId, PatientMedication medication, UUID customerId) {
        logger.info("Adding medication for patient: {}", patientId);
        
        // Verify patient ownership
        Patient patient = patientRepository.findByIdAndCustomerId(patientId, customerId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found or access denied"));
        
        medication.setPatient(patient);
        
        PatientMedication savedMedication = medicationRepository.save(medication);
        logger.info("Medication added successfully with ID: {}", savedMedication.getId());
        
        return savedMedication;
    }
    
    @Override
    public PatientMedication updateMedication(UUID medicationId, UUID patientId, PatientMedication medicationData, UUID customerId) {
        logger.info("Updating medication: {} for patient: {}", medicationId, patientId);
        
        // Verify ownership
        PatientMedication existingMedication = medicationRepository
            .findByIdAndPatientIdAndCustomerId(medicationId, patientId, customerId)
            .orElseThrow(() -> new IllegalArgumentException("Medication not found or access denied"));
        
        // Update fields
        existingMedication.setMedicationName(medicationData.getMedicationName());
        existingMedication.setDosage(medicationData.getDosage());
        existingMedication.setFrequency(medicationData.getFrequency());
        existingMedication.setPurpose(medicationData.getPurpose());
        existingMedication.setStartDate(medicationData.getStartDate());
        existingMedication.setEndDate(medicationData.getEndDate());
        existingMedication.setIsOngoing(medicationData.getIsOngoing());
        existingMedication.setPrescribingDoctor(medicationData.getPrescribingDoctor());
        existingMedication.setNotes(medicationData.getNotes());
        
        PatientMedication updated = medicationRepository.save(existingMedication);
        logger.info("Medication updated successfully: {}", medicationId);
        
        return updated;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PatientMedication> getMedicationById(UUID medicationId, UUID patientId, UUID customerId) {
        return medicationRepository.findByIdAndPatientIdAndCustomerId(medicationId, patientId, customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PatientMedication> getPatientMedications(UUID patientId, UUID customerId) {
        // Verify patient ownership
        if (!patientRepository.existsByIdAndCustomerId(patientId, customerId)) {
            throw new IllegalArgumentException("Patient not found or access denied");
        }
        
        return medicationRepository.findByPatientId(patientId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PatientMedication> getOngoingMedications(UUID patientId, UUID customerId) {
        // Verify patient ownership
        if (!patientRepository.existsByIdAndCustomerId(patientId, customerId)) {
            throw new IllegalArgumentException("Patient not found or access denied");
        }
        
        return medicationRepository.findByPatientIdAndIsOngoingTrue(patientId);
    }
    
    @Override
    public PatientMedication stopMedication(UUID medicationId, UUID patientId, UUID customerId) {
        logger.info("Stopping medication: {} for patient: {}", medicationId, patientId);
        
        PatientMedication medication = medicationRepository
            .findByIdAndPatientIdAndCustomerId(medicationId, patientId, customerId)
            .orElseThrow(() -> new IllegalArgumentException("Medication not found or access denied"));
        
        medication.stopMedication();
        
        PatientMedication stopped = medicationRepository.save(medication);
        logger.info("Medication stopped successfully: {}", medicationId);
        
        return stopped;
    }
    
    @Override
    public boolean deleteMedication(UUID medicationId, UUID patientId, UUID customerId) {
        logger.info("Deleting medication: {} for patient: {}", medicationId, patientId);
        
        Optional<PatientMedication> medicationOpt = medicationRepository
            .findByIdAndPatientIdAndCustomerId(medicationId, patientId, customerId);
        
        if (medicationOpt.isPresent()) {
            medicationRepository.delete(medicationOpt.get());
            logger.info("Medication deleted successfully: {}", medicationId);
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countOngoingMedications(UUID patientId, UUID customerId) {
        // Verify patient ownership
        if (!patientRepository.existsByIdAndCustomerId(patientId, customerId)) {
            throw new IllegalArgumentException("Patient not found or access denied");
        }
        
        return medicationRepository.countByPatientIdAndIsOngoingTrue(patientId);
    }
}

