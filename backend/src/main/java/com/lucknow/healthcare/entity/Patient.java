package com.lucknow.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Patient entity representing individuals receiving healthcare services
 * 
 * A patient is associated with a customer (user) and contains medical history,
 * personal information, and emergency contact details. Multiple patients can
 * belong to one customer (family members).
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patients_customer_id", columnList = "customer_id"),
    @Index(name = "idx_patients_is_active", columnList = "is_active"),
    @Index(name = "idx_patients_created_at", columnList = "created_at"),
    @Index(name = "idx_patients_customer_active", columnList = "customer_id, is_active")
})
@EntityListeners(AuditingEntityListener.class)
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    @Column(nullable = false)
    private Integer age;
    
    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be positive")
    @DecimalMax(value = "500.0", message = "Weight must be realistic")
    @Column(precision = 5, scale = 2)
    private BigDecimal weight; // in kg
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Height must be positive")
    @DecimalMax(value = "300.0", message = "Height must be realistic")
    @Column(precision = 5, scale = 2)
    private BigDecimal height; // in cm
    
    @Column(name = "blood_group", length = 10)
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-, UNKNOWN
    
    @Column(name = "is_diabetic")
    private Boolean isDiabetic = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "bp_status", length = 20)
    private BPStatus bpStatus = BPStatus.NORMAL;
    
    @Column(columnDefinition = "TEXT")
    private String allergies;
    
    @Column(name = "chronic_conditions", columnDefinition = "TEXT")
    private String chronicConditions;
    
    @Size(max = 255, message = "Emergency contact name too long")
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;
    
    @Size(max = 50, message = "Relation too long")
    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation; // Father, Mother, Spouse, Friend, etc.
    
    @NotNull(message = "Relationship to customer is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_to_customer", nullable = false, length = 50)
    private Relationship relationshipToCustomer;
    
    @Column(name = "is_sensitive_data")
    private Boolean isSensitiveData = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public enum BPStatus {
        NORMAL, HIGH, LOW, UNKNOWN
    }
    
    public enum Relationship {
        SELF, PARENT, CHILD, SPOUSE, SIBLING, GRANDPARENT, GRANDCHILD, OTHER
    }
    
    // Constructors
    
    public Patient() {}
    
    public Patient(User customer, String name, Integer age, Gender gender, Relationship relationshipToCustomer) {
        this.customer = customer;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.relationshipToCustomer = relationshipToCustomer;
    }
    
    // Business methods
    
    public boolean isMinor() {
        return age != null && age < 18;
    }
    
    public boolean hasMedicalConditions() {
        return (isDiabetic != null && isDiabetic) || 
               (bpStatus != null && bpStatus != BPStatus.NORMAL) ||
               (allergies != null && !allergies.trim().isEmpty()) ||
               (chronicConditions != null && !chronicConditions.trim().isEmpty());
    }
    
    public boolean hasEmergencyContact() {
        return emergencyContactName != null && !emergencyContactName.trim().isEmpty() &&
               emergencyContactPhone != null && !emergencyContactPhone.trim().isEmpty();
    }
    
    public String getDisplayName() {
        return name + " (" + age + "y, " + gender + ")";
    }
    
    // Getters and Setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getCustomer() {
        return customer;
    }
    
    public void setCustomer(User customer) {
        this.customer = customer;
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
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
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
    
    public BPStatus getBpStatus() {
        return bpStatus;
    }
    
    public void setBpStatus(BPStatus bpStatus) {
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
    
    public Relationship getRelationshipToCustomer() {
        return relationshipToCustomer;
    }
    
    public void setRelationshipToCustomer(Relationship relationshipToCustomer) {
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

