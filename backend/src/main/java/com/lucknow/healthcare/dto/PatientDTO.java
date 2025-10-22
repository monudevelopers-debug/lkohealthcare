package com.lucknow.healthcare.dto;

import com.lucknow.healthcare.entity.Patient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Patient entity
 * 
 * Provides a simplified representation of patient data for API responses.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public class PatientDTO {
    
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
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private Patient.Relationship relationshipToCustomer;
    private Boolean isSensitiveData;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PatientDTO() {}
    
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
}
