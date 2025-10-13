package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Review entity operations (Phase 1.5)
 * 
 * Defines business logic methods for review management including
 * CRUD operations, rating calculations, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface ReviewService {
    
    /**
     * Create a new review
     * 
     * @param review the review to create
     * @return the created review
     * @throws IllegalArgumentException if booking, user, or provider not found
     */
    Review createReview(Review review);
    
    /**
     * Find review by ID
     * 
     * @param id the review ID
     * @return Optional containing the review if found
     */
    Optional<Review> findById(UUID id);
    
    /**
     * Find review by booking ID
     * 
     * @param bookingId the booking ID
     * @return Optional containing the review if found
     */
    Optional<Review> findByBookingId(UUID bookingId);
    
    /**
     * Update review
     * 
     * @param review the review with updated information
     * @return the updated review
     * @throws IllegalArgumentException if review not found
     */
    Review updateReview(Review review);
    
    /**
     * Get reviews by user
     * 
     * @param userId the user ID
     * @return List of reviews by the specified user
     */
    List<Review> getReviewsByUser(UUID userId);
    
    /**
     * Get reviews by provider
     * 
     * @param providerId the provider ID
     * @return List of reviews for the specified provider
     */
    List<Review> getReviewsByProvider(UUID providerId);
    
    /**
     * Get reviews by rating
     * 
     * @param rating the rating to filter by
     * @return List of reviews with the specified rating
     */
    List<Review> getReviewsByRating(Integer rating);
    
    /**
     * Get reviews by rating range
     * 
     * @param minRating the minimum rating
     * @param maxRating the maximum rating
     * @return List of reviews within the rating range
     */
    List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating);
    
    /**
     * Get reviews by date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of reviews created within the date range
     */
    List<Review> getReviewsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Get all reviews with pagination
     * 
     * @param pageable pagination information
     * @return Page of reviews
     */
    Page<Review> getAllReviews(Pageable pageable);
    
    /**
     * Get reviews by user with pagination
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return Page of reviews by the specified user
     */
    Page<Review> getReviewsByUser(UUID userId, Pageable pageable);
    
    /**
     * Get reviews by provider with pagination
     * 
     * @param providerId the provider ID
     * @param pageable pagination information
     * @return Page of reviews for the specified provider
     */
    Page<Review> getReviewsByProvider(UUID providerId, Pageable pageable);
    
    /**
     * Get reviews by rating with pagination
     * 
     * @param rating the rating to filter by
     * @param pageable pagination information
     * @return Page of reviews with the specified rating
     */
    Page<Review> getReviewsByRating(Integer rating, Pageable pageable);
    
    /**
     * Get reviews by rating range with pagination
     * 
     * @param minRating the minimum rating
     * @param maxRating the maximum rating
     * @param pageable pagination information
     * @return Page of reviews within the rating range
     */
    Page<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating, Pageable pageable);
    
    /**
     * Count reviews by user
     * 
     * @param userId the user ID
     * @return number of reviews by the specified user
     */
    long countReviewsByUser(UUID userId);
    
    /**
     * Count reviews by provider
     * 
     * @param providerId the provider ID
     * @return number of reviews for the specified provider
     */
    long countReviewsByProvider(UUID providerId);
    
    /**
     * Count reviews by rating
     * 
     * @param rating the rating
     * @return number of reviews with the specified rating
     */
    long countReviewsByRating(Integer rating);
    
    /**
     * Calculate average rating by provider
     * 
     * @param providerId the provider ID
     * @return average rating for the specified provider
     */
    Double calculateAverageRatingByProvider(UUID providerId);
    
    /**
     * Calculate average rating by user
     * 
     * @param userId the user ID
     * @return average rating given by the specified user
     */
    Double calculateAverageRatingByUser(UUID userId);
    
    /**
     * Get top-rated providers
     * 
     * @param limit the maximum number of providers to return
     * @return List of top-rated providers with their average ratings
     */
    List<Review> getTopRatedProviders(int limit);
    
    /**
     * Get recent reviews
     * 
     * @param limit the maximum number of reviews to return
     * @return List of recent reviews
     */
    List<Review> getRecentReviews(int limit);
    
    /**
     * Delete review
     * 
     * @param id the review ID
     * @return true if review deleted successfully
     * @throws IllegalArgumentException if review not found
     */
    boolean deleteReview(UUID id);
}
