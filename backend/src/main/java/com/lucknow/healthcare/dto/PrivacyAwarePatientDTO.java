package com.lucknow.healthcare.dto;

import com.lucknow.healthcare.entity.Patient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Privacy-Aware Patient DTO for Provider Access
 * 
 * Implements customer privacy protection where providers can only see
 * emergency contact details within 24 hours before and after the service date.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public class PrivacyAwarePatientDTO {
    
    private UUID id;
    private UUID customerId;
    private String name;
    private Integer age;
    private Patient.Gender gender;
    private BigDecimal weight;
    private BigDecimal height;
    private String bloodGroup;
    private Boolean isDiabetic;
    private Patient.BPStatus bpStatus;
    private String allergies;
    private String chronicConditions;
    private String emergencyContactName;
    private String emergencyContactPhone; // May be null if outside privacy window
    private String emergencyContactRelation;
    private Patient.Relationship relationshipToCustomer;
    private Boolean isSensitiveData;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean emergencyContactAvailable; // Indicates if emergency contact is visible
    private String privacyMessage; // Explains why details are hidden
    
    // Constructors
    public PrivacyAwarePatientDTO() {}
    
    /**
     * Create privacy-aware patient DTO based on service date and requesting user role
     * 
     * @param patientDTO Original patient DTO
     * @param serviceDate Date of the service
     * @param requestingUserRole Role of the user requesting the data
     */
    public PrivacyAwarePatientDTO(PatientDTO patientDTO, LocalDate serviceDate, String requestingUserRole) {
        this.id = patientDTO.getId();
        this.customerId = patientDTO.getCustomerId();
        this.name = patientDTO.getName();
        this.age = patientDTO.getAge();
        this.gender = patientDTO.getGender();
        this.weight = patientDTO.getWeight();
        this.height = patientDTO.getHeight();
        this.bloodGroup = patientDTO.getBloodGroup();
        this.isDiabetic = patientDTO.getIsDiabetic();
        this.bpStatus = patientDTO.getBpStatus();
        this.allergies = patientDTO.getAllergies();
        this.chronicConditions = patientDTO.getChronicConditions();
        this.emergencyContactName = patientDTO.getEmergencyContactName();
        this.emergencyContactRelation = patientDTO.getEmergencyContactRelation();
        this.relationshipToCustomer = patientDTO.getRelationshipToCustomer();
        this.isSensitiveData = patientDTO.getIsSensitiveData();
        this.isActive = patientDTO.getIsActive();
        this.createdAt = patientDTO.getCreatedAt();
        this.updatedAt = patientDTO.getUpdatedAt();
        
        // Apply privacy rules only for providers
        if ("PROVIDER".equals(requestingUserRole)) {
            applyPrivacyRules(serviceDate);
        } else {
            // For admins and customers, always show emergency contact details
            this.emergencyContactPhone = patientDTO.getEmergencyContactPhone();
            this.emergencyContactAvailable = true;
            this.privacyMessage = "Emergency contact available";
        }
    }
    
    /**
     * Apply privacy rules based on service date
     * Emergency contact details are only visible 24 hours before and after service
     */
    private void applyPrivacyRules(LocalDate serviceDate) {
        LocalDate today = LocalDate.now();
        LocalDate dayBefore = serviceDate.minusDays(1);
        LocalDate dayAfter = serviceDate.plusDays(1);
        
        // Check if current date is within privacy window
        boolean isWithinPrivacyWindow = !today.isBefore(dayBefore) && !today.isAfter(dayAfter);
        
        if (isWithinPrivacyWindow) {
            // Within privacy window - show emergency contact details
            this.emergencyContactPhone = null; // Will be set from original DTO
            this.emergencyContactAvailable = true;
            this.privacyMessage = "Emergency contact available for service on " + serviceDate;
        } else {
            // Outside privacy window - hide emergency contact details
            this.emergencyContactPhone = null;
            this.emergencyContactAvailable = false;
            
            if (today.isBefore(dayBefore)) {
                this.privacyMessage = "Emergency contact will be available 24 hours before service";
            } else {
                this.privacyMessage = "Emergency contact is no longer available (expired 24 hours after service)";
            }
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Patient.Gender getGender() {
        return gender;
    }
    
    public void setGender(Patient.Gender gender) {
        this.gender = gender;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public BigDecimal getHeight() {
        return height;
    }
    
    public void setHeight(BigDecimal height) {
        this.height = height;
    }
    
    public String getBloodGroup() {
        return bloodGroup;
    }
    
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
    
    public Boolean getIsDiabetic() {
        return isDiabetic;
    }
    
    public void setIsDiabetic(Boolean isDiabetic) {
        this.isDiabetic = isDiabetic;
    }
    
    public Patient.BPStatus getBpStatus() {
        return bpStatus;
    }
    
    public void setBpStatus(Patient.BPStatus bpStatus) {
        this.bpStatus = bpStatus;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    public String getChronicConditions() {
        return chronicConditions;
    }
    
    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }
    
    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }
    
    public Patient.Relationship getRelationshipToCustomer() {
        return relationshipToCustomer;
    }
    
    public void setRelationshipToCustomer(Patient.Relationship relationshipToCustomer) {
        this.relationshipToCustomer = relationshipToCustomer;
    }
    
    public Boolean getIsSensitiveData() {
        return isSensitiveData;
    }
    
    public void setIsSensitiveData(Boolean isSensitiveData) {
        this.isSensitiveData = isSensitiveData;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public Boolean getEmergencyContactAvailable() {
        return emergencyContactAvailable;
    }
    
    public void setEmergencyContactAvailable(Boolean emergencyContactAvailable) {
        this.emergencyContactAvailable = emergencyContactAvailable;
    }
    
    public String getPrivacyMessage() {
        return privacyMessage;
    }
    
    public void setPrivacyMessage(String privacyMessage) {
        this.privacyMessage = privacyMessage;
    }
}
