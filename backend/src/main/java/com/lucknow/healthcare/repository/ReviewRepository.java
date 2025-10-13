package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Review;
import com.lucknow.healthcare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Review entity
 * 
 * Provides data access methods for review management including
 * provider ratings, user reviews, and rating-based queries.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    /**
     * Find review by booking
     * 
     * @param booking the booking to filter by
     * @return Optional containing the review if found
     */
    Optional<Review> findByBooking(Booking booking);
    
    /**
     * Find review by booking ID
     * 
     * @param bookingId the booking ID to filter by
     * @return Optional containing the review if found
     */
    Optional<Review> findByBookingId(UUID bookingId);
    
    /**
     * Find reviews by user
     * 
     * @param user the user to filter by
     * @return List of reviews by the specified user
     */
    List<Review> findByUser(User user);
    
    /**
     * Find reviews by user ID
     * 
     * @param userId the user ID to filter by
     * @return List of reviews by the specified user
     */
    List<Review> findByUserId(UUID userId);
    
    /**
     * Find reviews by provider
     * 
     * @param provider the provider to filter by
     * @return List of reviews for the specified provider
     */
    List<Review> findByProvider(Provider provider);
    
    /**
     * Find reviews by provider ID
     * 
     * @param providerId the provider ID to filter by
     * @return List of reviews for the specified provider
     */
    List<Review> findByProviderId(UUID providerId);
    
    /**
     * Find reviews by rating
     * 
     * @param rating the rating to filter by
     * @return List of reviews with the specified rating
     */
    List<Review> findByRating(Integer rating);
    
    /**
     * Find reviews by minimum rating
     * 
     * @param minRating the minimum rating to filter by
     * @return List of reviews with rating greater than or equal to minRating
     */
    List<Review> findByRatingGreaterThanEqual(Integer minRating);
    
    /**
     * Find reviews by maximum rating
     * 
     * @param maxRating the maximum rating to filter by
     * @return List of reviews with rating less than or equal to maxRating
     */
    List<Review> findByRatingLessThanEqual(Integer maxRating);
    
    /**
     * Find reviews by rating range
     * 
     * @param minRating the minimum rating
     * @param maxRating the maximum rating
     * @return List of reviews within the rating range
     */
    List<Review> findByRatingBetween(Integer minRating, Integer maxRating);
    
    /**
     * Find reviews by provider and rating
     * 
     * @param provider the provider to filter by
     * @param rating the rating to filter by
     * @return List of reviews for the specified provider and rating
     */
    List<Review> findByProviderAndRating(Provider provider, Integer rating);
    
    /**
     * Find reviews by provider ID and rating
     * 
     * @param providerId the provider ID to filter by
     * @param rating the rating to filter by
     * @return List of reviews for the specified provider and rating
     */
    List<Review> findByProviderIdAndRating(UUID providerId, Integer rating);
    
    /**
     * Find reviews by provider and minimum rating
     * 
     * @param provider the provider to filter by
     * @param minRating the minimum rating to filter by
     * @return List of reviews for the specified provider with rating >= minRating
     */
    List<Review> findByProviderAndRatingGreaterThanEqual(Provider provider, Integer minRating);
    
    /**
     * Find reviews by provider ID and minimum rating
     * 
     * @param providerId the provider ID to filter by
     * @param minRating the minimum rating to filter by
     * @return List of reviews for the specified provider with rating >= minRating
     */
    List<Review> findByProviderIdAndRatingGreaterThanEqual(UUID providerId, Integer minRating);
    
    /**
     * Find reviews created within a date range
     * 
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of reviews created within the date range
     */
    List<Review> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find reviews by provider and date range
     * 
     * @param provider the provider to filter by
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of reviews for the specified provider within the date range
     */
    List<Review> findByProviderAndCreatedAtBetween(Provider provider, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find reviews by user and date range
     * 
     * @param user the user to filter by
     * @param startDateTime the start date and time
     * @param endDateTime the end date and time
     * @return List of reviews by the specified user within the date range
     */
    List<Review> findByUserAndCreatedAtBetween(User user, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find top-rated reviews for a provider
     * 
     * @param provider the provider to filter by
     * @param limit the maximum number of reviews to return
     * @return List of top-rated reviews for the specified provider
     */
    @Query("SELECT r FROM Review r WHERE r.provider = :provider ORDER BY r.rating DESC, r.createdAt DESC")
    List<Review> findTopRatedReviewsByProvider(@Param("provider") Provider provider, @Param("limit") int limit);
    
    /**
     * Find recent reviews for a provider
     * 
     * @param provider the provider to filter by
     * @param limit the maximum number of reviews to return
     * @return List of recent reviews for the specified provider
     */
    @Query("SELECT r FROM Review r WHERE r.provider = :provider ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByProvider(@Param("provider") Provider provider, @Param("limit") int limit);
    
    /**
     * Find reviews with comments
     * 
     * @return List of reviews that have comments
     */
    @Query("SELECT r FROM Review r WHERE r.comment IS NOT NULL AND r.comment != ''")
    List<Review> findReviewsWithComments();
    
    /**
     * Find reviews with comments for a provider
     * 
     * @param provider the provider to filter by
     * @return List of reviews with comments for the specified provider
     */
    @Query("SELECT r FROM Review r WHERE r.provider = :provider AND r.comment IS NOT NULL AND r.comment != ''")
    List<Review> findReviewsWithCommentsByProvider(@Param("provider") Provider provider);
    
    /**
     * Count reviews by provider
     * 
     * @param provider the provider to count reviews for
     * @return number of reviews for the specified provider
     */
    long countByProvider(Provider provider);
    
    /**
     * Count reviews by provider ID
     * 
     * @param providerId the provider ID to count reviews for
     * @return number of reviews for the specified provider
     */
    long countByProviderId(UUID providerId);
    
    /**
     * Count reviews by user
     * 
     * @param user the user to count reviews for
     * @return number of reviews by the specified user
     */
    long countByUser(User user);
    
    /**
     * Count reviews by user ID
     * 
     * @param userId the user ID to count reviews for
     * @return number of reviews by the specified user
     */
    long countByUserId(UUID userId);
    
    /**
     * Count reviews by rating
     * 
     * @param rating the rating to count
     * @return number of reviews with the specified rating
     */
    long countByRating(Integer rating);
    
    /**
     * Count reviews by provider and rating
     * 
     * @param provider the provider to count reviews for
     * @param rating the rating to count
     * @return number of reviews for the specified provider and rating
     */
    long countByProviderAndRating(Provider provider, Integer rating);
    
    /**
     * Calculate average rating for a provider
     * 
     * @param provider the provider to calculate average rating for
     * @return average rating for the specified provider
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider = :provider")
    Double calculateAverageRatingByProvider(@Param("provider") Provider provider);
    
    /**
     * Calculate average rating for a provider ID
     * 
     * @param providerId the provider ID to calculate average rating for
     * @return average rating for the specified provider
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider.id = :providerId")
    Double calculateAverageRatingByProviderId(@Param("providerId") UUID providerId);
    
    /**
     * Check if review exists for booking
     * 
     * @param bookingId the booking ID to check
     * @return true if review exists for the booking, false otherwise
     */
    boolean existsByBookingId(UUID bookingId);
    
    /**
     * Check if user has reviewed provider
     * 
     * @param userId the user ID to check
     * @param providerId the provider ID to check
     * @return true if user has reviewed the provider, false otherwise
     */
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.user.id = :userId AND r.provider.id = :providerId")
    boolean existsByUserIdAndProviderId(@Param("userId") UUID userId, @Param("providerId") UUID providerId);
    
    // Pageable methods
    Page<Review> findByUserId(UUID userId, Pageable pageable);
    Page<Review> findByProviderId(UUID providerId, Pageable pageable);
    Page<Review> findByRating(Integer rating, Pageable pageable);
    Page<Review> findByRatingBetween(Integer minRating, Integer maxRating, Pageable pageable);
    
    // Additional query methods
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider.id = :providerId")
    Double calculateAverageRatingByProvider(@Param("providerId") UUID providerId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.user.id = :userId")
    Double calculateAverageRatingByUser(@Param("userId") UUID userId);
    
    @Query("SELECT r FROM Review r ORDER BY r.rating DESC")
    List<Review> findTopRatedProviders(@Param("limit") int limit);
    
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews(@Param("limit") int limit);
}
