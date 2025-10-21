package com.lucknow.healthcare.entity;

import com.lucknow.healthcare.enums.AvailabilityStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Provider entity representing healthcare service providers
 * 
 * This entity stores provider information including qualifications,
 * experience, availability, ratings, and verification status.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "providers", indexes = {
    @Index(name = "idx_provider_email", columnList = "email"),
    @Index(name = "idx_provider_availability", columnList = "availability_status"),
    @Index(name = "idx_provider_rating", columnList = "rating"),
    @Index(name = "idx_provider_verified", columnList = "is_verified"),
    @Index(name = "idx_provider_experience", columnList = "experience")
})
@EntityListeners(AuditingEntityListener.class)
public class Provider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Column(nullable = false, length = 15)
    private String phone;
    
    @NotBlank(message = "Qualification is required")
    @Size(max = 255, message = "Qualification must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String qualification;
    
    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    @Column(nullable = false)
    private Integer experience; // Experience in years
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;
    
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    @Column(nullable = false, precision = 3)
    private Double rating = 0.0;
    
    @Min(value = 0, message = "Total ratings cannot be negative")
    @Column(nullable = false)
    private Integer totalRatings = 0;
    
    @Column(nullable = false)
    private Boolean isVerified = false;
    
    @Column(columnDefinition = "JSONB")
    private List<String> documents; // Store verification documents
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "provider_services",
        joinColumns = @JoinColumn(name = "provider_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Service> services; // Services this provider can offer
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Provider() {}
    
    public Provider(String name, String email, String phone, String qualification, Integer experience) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.experience = experience;
    }
    
    // Business methods
    public boolean isAvailable(LocalDate date, LocalTime time) {
        return availabilityStatus == AvailabilityStatus.AVAILABLE;
    }
    
    public void updateRating(Integer newRating) {
        if (newRating != null && newRating >= 1 && newRating <= 5) {
            double totalRatingPoints = rating * totalRatings + newRating;
            totalRatings++;
            rating = totalRatingPoints / totalRatings;
        }
    }
    
    public Double getAverageRating() {
        return totalRatings > 0 ? rating : 0.0;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public Integer getExperience() {
        return experience;
    }
    
    public void setExperience(Integer experience) {
        this.experience = experience;
    }
    
    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }
    
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getTotalRatings() {
        return totalRatings;
    }
    
    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public List<String> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
    
    public List<Service> getServices() {
        return services;
    }
    
    public void setServices(List<Service> services) {
        this.services = services;
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
