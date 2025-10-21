package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.PatientMedication;
import com.lucknow.healthcare.service.interfaces.PatientMedicationService;
import com.lucknow.healthcare.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Patient Medication management
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/patients/{patientId}/medications")
@CrossOrigin(origins = "*")
public class PatientMedicationController {
    
    @Autowired
    private PatientMedicationService medicationService;
    
    /**
     * Add medication for a patient
     */
    @PostMapping
    public ResponseEntity<?> addMedication(
            @PathVariable UUID patientId,
            @Valid @RequestBody PatientMedication medication) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            PatientMedication created = medicationService.addMedication(patientId, medication, customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding medication: " + e.getMessage());
        }
    }
    
    /**
     * Get all medications for a patient
     */
    @GetMapping
    public ResponseEntity<?> getPatientMedications(@PathVariable UUID patientId) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            List<PatientMedication> medications = medicationService.getPatientMedications(patientId, customerId);
            return ResponseEntity.ok(medications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching medications: " + e.getMessage());
        }
    }
    
    /**
     * Get ongoing medications for a patient
     */
    @GetMapping("/ongoing")
    public ResponseEntity<?> getOngoingMedications(@PathVariable UUID patientId) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            List<PatientMedication> medications = medicationService.getOngoingMedications(patientId, customerId);
            return ResponseEntity.ok(medications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching medications: " + e.getMessage());
        }
    }
    
    /**
     * Get medication by ID
     */
    @GetMapping("/{medicationId}")
    public ResponseEntity<?> getMedicationById(
            @PathVariable UUID patientId,
            @PathVariable UUID medicationId) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            return medicationService.getMedicationById(medicationId, patientId, customerId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching medication: " + e.getMessage());
        }
    }
    
    /**
     * Update medication
     */
    @PutMapping("/{medicationId}")
    public ResponseEntity<?> updateMedication(
            @PathVariable UUID patientId,
            @PathVariable UUID medicationId,
            @Valid @RequestBody PatientMedication medication) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            PatientMedication updated = medicationService.updateMedication(medicationId, patientId, medication, customerId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating medication: " + e.getMessage());
        }
    }
    
    /**
     * Stop medication
     */
    @PostMapping("/{medicationId}/stop")
    public ResponseEntity<?> stopMedication(
            @PathVariable UUID patientId,
            @PathVariable UUID medicationId) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            PatientMedication stopped = medicationService.stopMedication(medicationId, patientId, customerId);
            return ResponseEntity.ok(stopped);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error stopping medication: " + e.getMessage());
        }
    }
    
    /**
     * Delete medication
     */
    @DeleteMapping("/{medicationId}")
    public ResponseEntity<?> deleteMedication(
            @PathVariable UUID patientId,
            @PathVariable UUID medicationId) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            boolean deleted = medicationService.deleteMedication(medicationId, patientId, customerId);
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting medication: " + e.getMessage());
        }
    }
    
    /**
     * Count ongoing medications
     */
    @GetMapping("/ongoing/count")
    public ResponseEntity<?> countOngoingMedications(@PathVariable UUID patientId) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            long count = medicationService.countOngoingMedications(patientId, customerId);
            return ResponseEntity.ok(new CountResponse(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error counting medications: " + e.getMessage());
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

