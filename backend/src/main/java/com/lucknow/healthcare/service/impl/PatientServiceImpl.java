package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Patient;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.repository.PatientRepository;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.service.interfaces.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Patient management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class PatientServiceImpl implements PatientService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Patient createPatient(Patient patient, UUID customerId) {
        logger.info("Creating patient for customer: {}", customerId);
        
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        patient.setCustomer(customer);
        patient.setIsActive(true);
        
        Patient savedPatient = patientRepository.save(patient);
        logger.info("Patient created successfully with ID: {}", savedPatient.getId());
        
        return savedPatient;
    }
    
    @Override
    public Patient updatePatient(UUID patientId, Patient patientData, UUID customerId) {
        logger.info("Updating patient: {} for customer: {}", patientId, customerId);
        
        Patient existingPatient = patientRepository.findByIdAndCustomerId(patientId, customerId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found or access denied"));
        
        // Update fields
        existingPatient.setName(patientData.getName());
        existingPatient.setAge(patientData.getAge());
        existingPatient.setGender(patientData.getGender());
        existingPatient.setWeight(patientData.getWeight());
        existingPatient.setHeight(patientData.getHeight());
        existingPatient.setBloodGroup(patientData.getBloodGroup());
        existingPatient.setIsDiabetic(patientData.getIsDiabetic());
        existingPatient.setBpStatus(patientData.getBpStatus());
        existingPatient.setAllergies(patientData.getAllergies());
        existingPatient.setChronicConditions(patientData.getChronicConditions());
        existingPatient.setEmergencyContactName(patientData.getEmergencyContactName());
        existingPatient.setEmergencyContactPhone(patientData.getEmergencyContactPhone());
        existingPatient.setEmergencyContactRelation(patientData.getEmergencyContactRelation());
        existingPatient.setRelationshipToCustomer(patientData.getRelationshipToCustomer());
        existingPatient.setIsSensitiveData(patientData.getIsSensitiveData());
        
        Patient updatedPatient = patientRepository.save(existingPatient);
        logger.info("Patient updated successfully: {}", patientId);
        
        return updatedPatient;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> getPatientById(UUID patientId, UUID customerId) {
        return patientRepository.findByIdAndCustomerId(patientId, customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Patient> getActivePatients(UUID customerId) {
        return patientRepository.findByCustomerIdAndIsActiveTrue(customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(UUID customerId, Pageable pageable) {
        return patientRepository.findByCustomerId(customerId, pageable);
    }
    
    @Override
    public boolean deletePatient(UUID patientId, UUID customerId) {
        logger.info("Soft deleting patient: {} for customer: {}", patientId, customerId);
        
        Optional<Patient> patientOpt = patientRepository.findByIdAndCustomerId(patientId, customerId);
        
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setIsActive(false);
            patientRepository.save(patient);
            logger.info("Patient soft deleted successfully: {}", patientId);
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Patient> searchPatients(UUID customerId, String name, Pageable pageable) {
        return patientRepository.searchByName(customerId, name, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Patient> getPatientsWithMedicalConditions(UUID customerId) {
        return patientRepository.findPatientsWithMedicalConditions(customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Patient> getMinorPatients(UUID customerId) {
        return patientRepository.findMinorPatients(customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActivePatients(UUID customerId) {
        return patientRepository.countByCustomerIdAndIsActiveTrue(customerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifyPatientOwnership(UUID patientId, UUID customerId) {
        return patientRepository.existsByIdAndCustomerId(patientId, customerId);
    }
}

