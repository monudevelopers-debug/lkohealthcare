package com.lucknow.healthcare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service entity representing individual healthcare services
 * 
 * This entity stores service information including name, description,
 * pricing, duration, and category relationship.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "services", indexes = {
    @Index(name = "idx_service_category_id", columnList = "category_id"),
    @Index(name = "idx_service_name", columnList = "name"),
    @Index(name = "idx_service_active", columnList = "is_active"),
    @Index(name = "idx_service_price", columnList = "price")
})
@EntityListeners(AuditingEntityListener.class)
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 hour")
    @Column(nullable = false)
    private Integer duration; // Duration in hours
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Service() {}
    
    public Service(ServiceCategory category, String name, String description, BigDecimal price, Integer duration) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }
    
    // Business methods
    public BigDecimal calculateTotalPrice(Integer requestedDuration) {
        if (requestedDuration == null || requestedDuration <= 0) {
            return price;
        }
        return price.multiply(BigDecimal.valueOf(requestedDuration));
    }
    
    public boolean isAvailable() {
        return isActive;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public ServiceCategory getCategory() {
        return category;
    }
    
    public void setCategory(ServiceCategory category) {
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
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
