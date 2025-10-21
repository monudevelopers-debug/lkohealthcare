package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Patient;
import com.lucknow.healthcare.service.interfaces.PatientService;
import com.lucknow.healthcare.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Patient management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = "*")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    /**
     * Create a new patient
     */
    @PostMapping
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            Patient createdPatient = patientService.createPatient(patient, customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating patient: " + e.getMessage());
        }
    }
    
    /**
     * Get all active patients for current customer
     */
    @GetMapping
    public ResponseEntity<?> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            
            if (page == -1) {
                // Return all patients without pagination
                List<Patient> patients = patientService.getActivePatients(customerId);
                return ResponseEntity.ok(patients);
            } else {
                // Return paginated results
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<Patient> patients = patientService.getAllPatients(customerId, pageable);
                return ResponseEntity.ok(patients);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching patients: " + e.getMessage());
        }
    }
    
    /**
     * Get patient by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            return patientService.getPatientById(id, customerId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching patient: " + e.getMessage());
        }
    }
    
    /**
     * Update patient
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody Patient patient) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            Patient updatedPatient = patientService.updatePatient(id, patient, customerId);
            return ResponseEntity.ok(updatedPatient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating patient: " + e.getMessage());
        }
    }
    
    /**
     * Soft delete patient
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            boolean deleted = patientService.deletePatient(id, customerId);
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting patient: " + e.getMessage());
        }
    }
    
    /**
     * Search patients by name
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPatients(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<Patient> patients = patientService.searchPatients(customerId, name, pageable);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching patients: " + e.getMessage());
        }
    }
    
    /**
     * Get patients with medical conditions
     */
    @GetMapping("/with-conditions")
    public ResponseEntity<?> getPatientsWithConditions() {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            List<Patient> patients = patientService.getPatientsWithMedicalConditions(customerId);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching patients: " + e.getMessage());
        }
    }
    
    /**
     * Get minor patients
     */
    @GetMapping("/minors")
    public ResponseEntity<?> getMinorPatients() {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            List<Patient> patients = patientService.getMinorPatients(customerId);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching patients: " + e.getMessage());
        }
    }
    
    /**
     * Get patient count
     */
    @GetMapping("/count")
    public ResponseEntity<?> getPatientCount() {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            long count = patientService.countActivePatients(customerId);
            return ResponseEntity.ok(new CountResponse(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error counting patients: " + e.getMessage());
        }
    }
    
    // Response classes
    
    static class CountResponse {
        private long count;
        
        public CountResponse(long count) {
            this.count = count;
        }
        
        public long getCount() {
            return count;
        }
        
        public void setCount(long count) {
            this.count = count;
        }
    }
}

