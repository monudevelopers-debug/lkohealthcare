package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import com.lucknow.healthcare.repository.ProviderRepository;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Provider entity operations
 * 
 * Implements business logic for provider management including
 * CRUD operations, availability management, rating updates, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ProviderServiceImpl implements ProviderService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Override
    public Provider createProvider(Provider provider) {
        // Check if email already exists
        if (providerRepository.findByEmail(provider.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Provider email already exists: " + provider.getEmail());
        }
        
        // Set default values
        provider.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        provider.setRating(0.0);
        provider.setTotalRatings(0);
        provider.setIsVerified(false);
        
        return providerRepository.save(provider);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Provider> findById(UUID id) {
        return providerRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Provider> findByEmail(String email) {
        return providerRepository.findByEmail(email);
    }
    
    @Override
    public Provider updateProvider(Provider provider) {
        if (!providerRepository.existsById(provider.getId())) {
            throw new IllegalArgumentException("Provider not found with ID: " + provider.getId());
        }
        
        return providerRepository.save(provider);
    }
    
    @Override
    public Provider updateProviderAvailability(UUID id, AvailabilityStatus availabilityStatus) {
        Optional<Provider> providerOpt = providerRepository.findById(id);
        
        if (providerOpt.isEmpty()) {
            throw new IllegalArgumentException("Provider not found with ID: " + id);
        }
        
        Provider provider = providerOpt.get();
        provider.setAvailabilityStatus(availabilityStatus);
        
        return providerRepository.save(provider);
    }
    
    @Override
    public Provider updateProviderVerification(UUID id, Boolean isVerified) {
        Optional<Provider> providerOpt = providerRepository.findById(id);
        
        if (providerOpt.isEmpty()) {
            throw new IllegalArgumentException("Provider not found with ID: " + id);
        }
        
        Provider provider = providerOpt.get();
        provider.setIsVerified(isVerified);
        
        return providerRepository.save(provider);
    }
    
    @Override
    public Provider updateProviderRating(UUID id, Integer rating) {
        Optional<Provider> providerOpt = providerRepository.findById(id);
        
        if (providerOpt.isEmpty()) {
            throw new IllegalArgumentException("Provider not found with ID: " + id);
        }
        
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        Provider provider = providerOpt.get();
        provider.updateRating(rating);
        
        return providerRepository.save(provider);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getProvidersByAvailability(AvailabilityStatus availabilityStatus) {
        return providerRepository.findByAvailabilityStatus(availabilityStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getProvidersByVerification(Boolean isVerified) {
        return providerRepository.findByIsVerified(isVerified);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getVerifiedProviders() {
        return providerRepository.findByIsVerifiedTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getAvailableAndVerifiedProviders() {
        return providerRepository.findAvailableAndVerifiedProviders();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getProvidersByMinRating(Double minRating) {
        return providerRepository.findByRatingGreaterThanEqual(minRating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getAvailableProvidersByMinRating(Double minRating) {
        return providerRepository.findAvailableByRatingGreaterThanEqual(minRating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getProvidersByExperienceRange(Integer minExperience, Integer maxExperience) {
        return providerRepository.findByExperienceRange(minExperience, maxExperience);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getAvailableProvidersByExperienceRange(Integer minExperience, Integer maxExperience) {
        return providerRepository.findAvailableByExperienceRange(minExperience, maxExperience);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> searchProvidersByName(String name) {
        return providerRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> searchProvidersByQualification(String qualification) {
        return providerRepository.findByQualificationContainingIgnoreCase(qualification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getTopRatedProviders(int limit) {
        return providerRepository.findTopRatedProviders(limit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Provider> getAvailableProvidersForDateTime(LocalDate date, LocalTime time) {
        return providerRepository.findAvailableProvidersForDateTime(date, time);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Provider> getAllProviders(Pageable pageable) {
        return providerRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Provider> getProvidersByAvailability(AvailabilityStatus availabilityStatus, Pageable pageable) {
        return providerRepository.findByAvailabilityStatus(availabilityStatus, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Provider> getProvidersByVerification(Boolean isVerified, Pageable pageable) {
        return providerRepository.findByIsVerified(isVerified, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Provider> getAvailableAndVerifiedProviders(Pageable pageable) {
        return providerRepository.findAvailableAndVerifiedProviders(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countProvidersByAvailability(AvailabilityStatus availabilityStatus) {
        return providerRepository.countByAvailabilityStatus(availabilityStatus);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countProvidersByVerification(Boolean isVerified) {
        return providerRepository.countByIsVerified(isVerified);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAvailableAndVerifiedProviders() {
        return providerRepository.countAvailableAndVerifiedProviders();
    }
    
    @Override
    public boolean deleteProvider(UUID id) {
        Optional<Provider> providerOpt = providerRepository.findById(id);
        
        if (providerOpt.isEmpty()) {
            throw new IllegalArgumentException("Provider not found with ID: " + id);
        }
        
        Provider provider = providerOpt.get();
        provider.setAvailabilityStatus(AvailabilityStatus.ON_LEAVE);
        
        providerRepository.save(provider);
        return true;
    }
}
