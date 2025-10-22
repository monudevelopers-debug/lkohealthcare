package com.lucknow.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Review entity representing customer feedback (Phase 1.5)
 * 
 * This entity stores review information including rating, comment,
 * and relationships to booking, user, and provider.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_booking_id", columnList = "booking_id"),
    @Index(name = "idx_review_user_id", columnList = "user_id"),
    @Index(name = "idx_review_provider_id", columnList = "provider_id"),
    @Index(name = "idx_review_rating", columnList = "rating"),
    @Index(name = "idx_review_created_at", columnList = "created_at"),
    @Index(name = "idx_review_provider_rating", columnList = "provider_id, rating")
})
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @JsonIgnore
    private Booking booking;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @NotNull(message = "Provider is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    @JsonIgnore
    private Provider provider;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    @Column(nullable = false)
    private Integer rating;
    
    @Size(max = 1000, message = "Review comment must not exceed 1000 characters")
    @Column(length = 1000)
    private String comment;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Review() {}
    
    public Review(Booking booking, User user, Provider provider, Integer rating, String comment) {
        this.booking = booking;
        this.user = user;
        this.provider = provider;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Business methods
    public boolean isValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
