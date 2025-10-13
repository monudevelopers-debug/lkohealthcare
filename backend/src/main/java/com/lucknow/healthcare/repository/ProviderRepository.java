package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Provider entity
 * 
 * Provides data access methods for provider management including
 * availability queries, rating-based searches, and verification status.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    
    /**
     * Find provider by email
     * 
     * @param email the email address to search for
     * @return Optional containing the provider if found
     */
    Optional<Provider> findByEmail(String email);
    
    /**
     * Find providers by availability status
     * 
     * @param availabilityStatus the availability status to filter by
     * @return List of providers with the specified availability status
     */
    List<Provider> findByAvailabilityStatus(AvailabilityStatus availabilityStatus);
    
    /**
     * Find providers by verification status
     * 
     * @param isVerified the verification status to filter by
     * @return List of providers with the specified verification status
     */
    List<Provider> findByIsVerified(Boolean isVerified);
    
    /**
     * Find verified providers
     * 
     * @return List of verified providers
     */
    List<Provider> findByIsVerifiedTrue();
    
    /**
     * Find available and verified providers
     * 
     * @return List of available and verified providers
     */
    @Query("SELECT p FROM Provider p WHERE p.availabilityStatus = 'AVAILABLE' AND p.isVerified = true")
    List<Provider> findAvailableAndVerifiedProviders();
    
    /**
     * Find providers by minimum rating
     * 
     * @param minRating the minimum rating to filter by
     * @return List of providers with rating greater than or equal to minRating
     */
    @Query("SELECT p FROM Provider p WHERE p.rating >= :minRating")
    List<Provider> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);
    
    /**
     * Find available providers by minimum rating
     * 
     * @param minRating the minimum rating to filter by
     * @return List of available providers with rating greater than or equal to minRating
     */
    @Query("SELECT p FROM Provider p WHERE p.availabilityStatus = 'AVAILABLE' AND p.rating >= :minRating")
    List<Provider> findAvailableByRatingGreaterThanEqual(@Param("minRating") Double minRating);
    
    /**
     * Find providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return List of providers within the experience range
     */
    @Query("SELECT p FROM Provider p WHERE p.experience BETWEEN :minExperience AND :maxExperience")
    List<Provider> findByExperienceRange(@Param("minExperience") Integer minExperience, 
                                       @Param("maxExperience") Integer maxExperience);
    
    /**
     * Find available providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return List of available providers within the experience range
     */
    @Query("SELECT p FROM Provider p WHERE p.availabilityStatus = 'AVAILABLE' AND p.experience BETWEEN :minExperience AND :maxExperience")
    List<Provider> findAvailableByExperienceRange(@Param("minExperience") Integer minExperience, 
                                                 @Param("maxExperience") Integer maxExperience);
    
    /**
     * Find providers by name containing (case-insensitive)
     * 
     * @param name the name to search for
     * @return List of providers whose name contains the search term
     */
    @Query("SELECT p FROM Provider p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Provider> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find providers by qualification containing (case-insensitive)
     * 
     * @param qualification the qualification to search for
     * @return List of providers whose qualification contains the search term
     */
    @Query("SELECT p FROM Provider p WHERE LOWER(p.qualification) LIKE LOWER(CONCAT('%', :qualification, '%'))")
    List<Provider> findByQualificationContainingIgnoreCase(@Param("qualification") String qualification);
    
    /**
     * Find top-rated providers
     * 
     * @param limit the maximum number of providers to return
     * @return List of top-rated providers
     */
    @Query("SELECT p FROM Provider p WHERE p.isVerified = true ORDER BY p.rating DESC, p.totalRatings DESC")
    List<Provider> findTopRatedProviders(@Param("limit") int limit);
    
    /**
     * Find available providers for a specific date and time
     * This is a placeholder for more complex availability logic
     * 
     * @param date the date to check availability
     * @param time the time to check availability
     * @return List of available providers for the specified date and time
     */
    @Query("SELECT p FROM Provider p WHERE p.availabilityStatus = 'AVAILABLE' AND p.isVerified = true")
    List<Provider> findAvailableProvidersForDateTime(@Param("date") LocalDate date, @Param("time") LocalTime time);
    
    /**
     * Count providers by availability status
     * 
     * @param availabilityStatus the availability status to count
     * @return number of providers with the specified availability status
     */
    long countByAvailabilityStatus(AvailabilityStatus availabilityStatus);
    
    /**
     * Count providers by verification status
     * 
     * @param isVerified the verification status to count
     * @return number of providers with the specified verification status
     */
    long countByIsVerified(Boolean isVerified);
    
    /**
     * Count available and verified providers
     * 
     * @return number of available and verified providers
     */
    @Query("SELECT COUNT(p) FROM Provider p WHERE p.availabilityStatus = 'AVAILABLE' AND p.isVerified = true")
    long countAvailableAndVerifiedProviders();
}
