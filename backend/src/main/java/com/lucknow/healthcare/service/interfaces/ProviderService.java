package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Provider entity operations
 * 
 * Defines business logic methods for provider management including
 * CRUD operations, availability management, rating updates, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface ProviderService {
    
    /**
     * Create a new provider
     * 
     * @param provider the provider to create
     * @return the created provider
     * @throws IllegalArgumentException if email already exists
     */
    Provider createProvider(Provider provider);
    
    /**
     * Find provider by ID
     * 
     * @param id the provider ID
     * @return Optional containing the provider if found
     */
    Optional<Provider> findById(UUID id);
    
    /**
     * Find provider by email
     * 
     * @param email the provider's email
     * @return Optional containing the provider if found
     */
    Optional<Provider> findByEmail(String email);
    
    /**
     * Update provider
     * 
     * @param provider the provider with updated information
     * @return the updated provider
     * @throws IllegalArgumentException if provider not found
     */
    Provider updateProvider(Provider provider);
    
    /**
     * Update provider availability status
     * 
     * @param id the provider ID
     * @param availabilityStatus the new availability status
     * @return the updated provider
     * @throws IllegalArgumentException if provider not found
     */
    Provider updateProviderAvailability(UUID id, AvailabilityStatus availabilityStatus);
    
    /**
     * Update provider verification status
     * 
     * @param id the provider ID
     * @param isVerified the new verification status
     * @return the updated provider
     * @throws IllegalArgumentException if provider not found
     */
    Provider updateProviderVerification(UUID id, Boolean isVerified);
    
    /**
     * Update provider rating
     * 
     * @param id the provider ID
     * @param rating the new rating (1-5)
     * @return the updated provider
     * @throws IllegalArgumentException if provider not found or rating is invalid
     */
    Provider updateProviderRating(UUID id, Integer rating);
    
    /**
     * Get all providers
     * 
     * @return List of all providers
     */
    List<Provider> getAllProviders();
    
    /**
     * Get providers by availability status
     * 
     * @param availabilityStatus the availability status
     * @return List of providers with the specified availability status
     */
    List<Provider> getProvidersByAvailability(AvailabilityStatus availabilityStatus);
    
    /**
     * Get providers by verification status
     * 
     * @param isVerified the verification status
     * @return List of providers with the specified verification status
     */
    List<Provider> getProvidersByVerification(Boolean isVerified);
    
    /**
     * Get verified providers
     * 
     * @return List of verified providers
     */
    List<Provider> getVerifiedProviders();
    
    /**
     * Get available and verified providers
     * 
     * @return List of available and verified providers
     */
    List<Provider> getAvailableAndVerifiedProviders();
    
    /**
     * Get providers by minimum rating
     * 
     * @param minRating the minimum rating
     * @return List of providers with rating greater than or equal to minRating
     */
    List<Provider> getProvidersByMinRating(Double minRating);
    
    /**
     * Get available providers by minimum rating
     * 
     * @param minRating the minimum rating
     * @return List of available providers with rating greater than or equal to minRating
     */
    List<Provider> getAvailableProvidersByMinRating(Double minRating);
    
    /**
     * Get providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return List of providers within the experience range
     */
    List<Provider> getProvidersByExperienceRange(Integer minExperience, Integer maxExperience);
    
    /**
     * Get available providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return List of available providers within the experience range
     */
    List<Provider> getAvailableProvidersByExperienceRange(Integer minExperience, Integer maxExperience);
    
    /**
     * Search providers by name
     * 
     * @param name the name to search for
     * @return List of providers whose name contains the search term
     */
    List<Provider> searchProvidersByName(String name);
    
    /**
     * Search providers by qualification
     * 
     * @param qualification the qualification to search for
     * @return List of providers whose qualification contains the search term
     */
    List<Provider> searchProvidersByQualification(String qualification);
    
    /**
     * Get top-rated providers
     * 
     * @param limit the maximum number of providers to return
     * @return List of top-rated providers
     */
    List<Provider> getTopRatedProviders(int limit);
    
    /**
     * Get available providers for a specific date and time
     * 
     * @param date the date to check availability
     * @param time the time to check availability
     * @return List of available providers for the specified date and time
     */
    List<Provider> getAvailableProvidersForDateTime(LocalDate date, LocalTime time);
    
    /**
     * Get all providers with pagination
     * 
     * @param pageable pagination information
     * @return Page of providers
     */
    Page<Provider> getAllProviders(Pageable pageable);
    
    /**
     * Get providers by availability status with pagination
     * 
     * @param availabilityStatus the availability status
     * @param pageable pagination information
     * @return Page of providers with the specified availability status
     */
    Page<Provider> getProvidersByAvailability(AvailabilityStatus availabilityStatus, Pageable pageable);
    
    /**
     * Get providers by verification status with pagination
     * 
     * @param isVerified the verification status
     * @param pageable pagination information
     * @return Page of providers with the specified verification status
     */
    Page<Provider> getProvidersByVerification(Boolean isVerified, Pageable pageable);
    
    /**
     * Get available and verified providers with pagination
     * 
     * @param pageable pagination information
     * @return Page of available and verified providers
     */
    Page<Provider> getAvailableAndVerifiedProviders(Pageable pageable);
    
    /**
     * Count providers by availability status
     * 
     * @param availabilityStatus the availability status
     * @return number of providers with the specified availability status
     */
    long countProvidersByAvailability(AvailabilityStatus availabilityStatus);
    
    /**
     * Count providers by verification status
     * 
     * @param isVerified the verification status
     * @return number of providers with the specified verification status
     */
    long countProvidersByVerification(Boolean isVerified);
    
    /**
     * Count available and verified providers
     * 
     * @return number of available and verified providers
     */
    long countAvailableAndVerifiedProviders();
    
    /**
     * Delete provider (soft delete by setting availability to UNAVAILABLE)
     * 
     * @param id the provider ID
     * @return true if provider deleted successfully
     * @throws IllegalArgumentException if provider not found
     */
    boolean deleteProvider(UUID id);
}
