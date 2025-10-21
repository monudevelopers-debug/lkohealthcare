package com.lucknow.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PatientMedication entity representing medications taken by patients
 * 
 * Stores current and past medication information including dosage, frequency,
 * and purpose. Helps providers understand patient's medical background.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "patient_medications", indexes = {
    @Index(name = "idx_patient_medications_patient_id", columnList = "patient_id"),
    @Index(name = "idx_patient_medications_is_ongoing", columnList = "is_ongoing"),
    @Index(name = "idx_patient_medications_patient_ongoing", columnList = "patient_id, is_ongoing")
})
@EntityListeners(AuditingEntityListener.class)
public class PatientMedication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @NotBlank(message = "Medication name is required")
    @Size(min = 2, max = 255, message = "Medication name must be between 2 and 255 characters")
    @Column(name = "medication_name", nullable = false)
    private String medicationName;
    
    @Size(max = 100, message = "Dosage description too long")
    @Column(length = 100)
    private String dosage; // e.g., "500mg", "2 tablets", "5ml"
    
    @Size(max = 100, message = "Frequency description too long")
    @Column(length = 100)
    private String frequency; // e.g., "Twice daily", "As needed", "Every 8 hours"
    
    @Column(columnDefinition = "TEXT")
    private String purpose; // Why the medication is taken
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_ongoing")
    private Boolean isOngoing = true;
    
    @Size(max = 255, message = "Doctor name too long")
    @Column(name = "prescribing_doctor")
    private String prescribingDoctor;
    
    @Column(columnDefinition = "TEXT")
    private String notes; // Additional notes about the medication
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    
    public PatientMedication() {}
    
    public PatientMedication(Patient patient, String medicationName, String dosage, String frequency) {
        this.patient = patient;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
    }
    
    // Business methods
    
    public boolean isCurrentlyTaken() {
        return isOngoing != null && isOngoing && 
               (endDate == null || endDate.isAfter(LocalDate.now()));
    }
    
    public boolean hasExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }
    
    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder(medicationName);
        if (dosage != null) {
            info.append(" - ").append(dosage);
        }
        if (frequency != null) {
            info.append(" (").append(frequency).append(")");
        }
        return info.toString();
    }
    
    public void stopMedication() {
        this.isOngoing = false;
        if (this.endDate == null) {
            this.endDate = LocalDate.now();
        }
    }
    
    // Getters and Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public String getMedicationName() {
        return medicationName;
    }
    
    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Boolean getIsOngoing() {
        return isOngoing;
    }
    
    public void setIsOngoing(Boolean isOngoing) {
        this.isOngoing = isOngoing;
    }
    
    public String getPrescribingDoctor() {
        return prescribingDoctor;
    }
    
    public void setPrescribingDoctor(String prescribingDoctor) {
        this.prescribingDoctor = prescribingDoctor;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

