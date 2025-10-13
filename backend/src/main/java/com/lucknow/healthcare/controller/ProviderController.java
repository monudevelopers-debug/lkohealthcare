package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Provider entity operations
 * 
 * Provides REST endpoints for provider management including
 * CRUD operations, availability management, rating updates, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*")
public class ProviderController {
    
    @Autowired
    private ProviderService providerService;
    
    /**
     * Create a new provider
     * 
     * @param provider the provider to create
     * @return ResponseEntity containing the created provider
     */
    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        try {
            Provider createdProvider = providerService.createProvider(provider);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get provider by ID
     * 
     * @param id the provider ID
     * @return ResponseEntity containing the provider if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable UUID id) {
        Optional<Provider> providerOpt = providerService.findById(id);
        return providerOpt.map(provider -> ResponseEntity.ok(provider))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get provider by email
     * 
     * @param email the provider's email
     * @return ResponseEntity containing the provider if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Provider> getProviderByEmail(@PathVariable String email) {
        Optional<Provider> providerOpt = providerService.findByEmail(email);
        return providerOpt.map(provider -> ResponseEntity.ok(provider))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update provider
     * 
     * @param id the provider ID
     * @param provider the updated provider information
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(@PathVariable UUID id, @RequestBody Provider provider) {
        try {
            provider.setId(id);
            Provider updatedProvider = providerService.updateProvider(provider);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update provider availability status
     * 
     * @param id the provider ID
     * @param availabilityStatus the new availability status
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<Provider> updateProviderAvailability(@PathVariable UUID id, @RequestParam AvailabilityStatus availabilityStatus) {
        try {
            Provider updatedProvider = providerService.updateProviderAvailability(id, availabilityStatus);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update provider verification status
     * 
     * @param id the provider ID
     * @param isVerified the new verification status
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}/verification")
    public ResponseEntity<Provider> updateProviderVerification(@PathVariable UUID id, @RequestParam Boolean isVerified) {
        try {
            Provider updatedProvider = providerService.updateProviderVerification(id, isVerified);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update provider rating
     * 
     * @param id the provider ID
     * @param rating the new rating (1-5)
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}/rating")
    public ResponseEntity<Provider> updateProviderRating(@PathVariable UUID id, @RequestParam Integer rating) {
        try {
            Provider updatedProvider = providerService.updateProviderRating(id, rating);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all providers
     * 
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by availability status
     * 
     * @param availabilityStatus the availability status
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/availability/{availabilityStatus}")
    public ResponseEntity<List<Provider>> getProvidersByAvailability(@PathVariable AvailabilityStatus availabilityStatus) {
        List<Provider> providers = providerService.getProvidersByAvailability(availabilityStatus);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by verification status
     * 
     * @param isVerified the verification status
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/verification/{isVerified}")
    public ResponseEntity<List<Provider>> getProvidersByVerification(@PathVariable Boolean isVerified) {
        List<Provider> providers = providerService.getProvidersByVerification(isVerified);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get verified providers
     * 
     * @return ResponseEntity containing the list of verified providers
     */
    @GetMapping("/verified")
    public ResponseEntity<List<Provider>> getVerifiedProviders() {
        List<Provider> providers = providerService.getVerifiedProviders();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available and verified providers
     * 
     * @return ResponseEntity containing the list of available and verified providers
     */
    @GetMapping("/available-verified")
    public ResponseEntity<List<Provider>> getAvailableAndVerifiedProviders() {
        List<Provider> providers = providerService.getAvailableAndVerifiedProviders();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by minimum rating
     * 
     * @param minRating the minimum rating
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<Provider>> getProvidersByMinRating(@PathVariable Double minRating) {
        List<Provider> providers = providerService.getProvidersByMinRating(minRating);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available providers by minimum rating
     * 
     * @param minRating the minimum rating
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/available/rating/{minRating}")
    public ResponseEntity<List<Provider>> getAvailableProvidersByMinRating(@PathVariable Double minRating) {
        List<Provider> providers = providerService.getAvailableProvidersByMinRating(minRating);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/experience")
    public ResponseEntity<List<Provider>> getProvidersByExperienceRange(@RequestParam Integer minExperience, @RequestParam Integer maxExperience) {
        List<Provider> providers = providerService.getProvidersByExperienceRange(minExperience, maxExperience);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/available/experience")
    public ResponseEntity<List<Provider>> getAvailableProvidersByExperienceRange(@RequestParam Integer minExperience, @RequestParam Integer maxExperience) {
        List<Provider> providers = providerService.getAvailableProvidersByExperienceRange(minExperience, maxExperience);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Search providers by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/search")
    public ResponseEntity<List<Provider>> searchProvidersByName(@RequestParam String name) {
        List<Provider> providers = providerService.searchProvidersByName(name);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Search providers by qualification
     * 
     * @param qualification the qualification to search for
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/search/qualification")
    public ResponseEntity<List<Provider>> searchProvidersByQualification(@RequestParam String qualification) {
        List<Provider> providers = providerService.searchProvidersByQualification(qualification);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get top-rated providers
     * 
     * @param limit the maximum number of providers to return
     * @return ResponseEntity containing the list of top-rated providers
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Provider>> getTopRatedProviders(@RequestParam(defaultValue = "10") int limit) {
        List<Provider> providers = providerService.getTopRatedProviders(limit);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available providers for a specific date and time
     * 
     * @param date the date to check availability
     * @param time the time to check availability
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/available")
    public ResponseEntity<List<Provider>> getAvailableProvidersForDateTime(@RequestParam LocalDate date, @RequestParam LocalTime time) {
        List<Provider> providers = providerService.getAvailableProvidersForDateTime(date, time);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get all providers with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of providers
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Provider>> getAllProviders(Pageable pageable) {
        Page<Provider> providers = providerService.getAllProviders(pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by availability status with pagination
     * 
     * @param availabilityStatus the availability status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of providers
     */
    @GetMapping("/availability/{availabilityStatus}/page")
    public ResponseEntity<Page<Provider>> getProvidersByAvailability(@PathVariable AvailabilityStatus availabilityStatus, Pageable pageable) {
        Page<Provider> providers = providerService.getProvidersByAvailability(availabilityStatus, pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by verification status with pagination
     * 
     * @param isVerified the verification status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of providers
     */
    @GetMapping("/verification/{isVerified}/page")
    public ResponseEntity<Page<Provider>> getProvidersByVerification(@PathVariable Boolean isVerified, Pageable pageable) {
        Page<Provider> providers = providerService.getProvidersByVerification(isVerified, pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available and verified providers with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of available and verified providers
     */
    @GetMapping("/available-verified/page")
    public ResponseEntity<Page<Provider>> getAvailableAndVerifiedProviders(Pageable pageable) {
        Page<Provider> providers = providerService.getAvailableAndVerifiedProviders(pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Count providers by availability status
     * 
     * @param availabilityStatus the availability status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/availability/{availabilityStatus}")
    public ResponseEntity<Long> countProvidersByAvailability(@PathVariable AvailabilityStatus availabilityStatus) {
        long count = providerService.countProvidersByAvailability(availabilityStatus);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count providers by verification status
     * 
     * @param isVerified the verification status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/verification/{isVerified}")
    public ResponseEntity<Long> countProvidersByVerification(@PathVariable Boolean isVerified) {
        long count = providerService.countProvidersByVerification(isVerified);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count available and verified providers
     * 
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/available-verified")
    public ResponseEntity<Long> countAvailableAndVerifiedProviders() {
        long count = providerService.countAvailableAndVerifiedProviders();
        return ResponseEntity.ok(count);
    }
    
    /**
     * Delete provider
     * 
     * @param id the provider ID
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID id) {
        try {
            boolean success = providerService.deleteProvider(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
