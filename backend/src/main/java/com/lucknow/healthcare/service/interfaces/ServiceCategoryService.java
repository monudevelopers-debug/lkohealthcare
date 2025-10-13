package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for ServiceCategory entity operations
 * 
 * Defines business logic methods for service category management including
 * CRUD operations, active category queries, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface ServiceCategoryService {
    
    /**
     * Create a new service category
     * 
     * @param serviceCategory the service category to create
     * @return the created service category
     * @throws IllegalArgumentException if category name already exists
     */
    ServiceCategory createServiceCategory(ServiceCategory serviceCategory);
    
    /**
     * Find service category by ID
     * 
     * @param id the service category ID
     * @return Optional containing the service category if found
     */
    Optional<ServiceCategory> findById(UUID id);
    
    /**
     * Find service category by name
     * 
     * @param name the service category name
     * @return Optional containing the service category if found
     */
    Optional<ServiceCategory> findByName(String name);
    
    /**
     * Update service category
     * 
     * @param serviceCategory the service category with updated information
     * @return the updated service category
     * @throws IllegalArgumentException if service category not found
     */
    ServiceCategory updateServiceCategory(ServiceCategory serviceCategory);
    
    /**
     * Update service category status
     * 
     * @param id the service category ID
     * @param isActive the new active status
     * @return the updated service category
     * @throws IllegalArgumentException if service category not found
     */
    ServiceCategory updateServiceCategoryStatus(UUID id, Boolean isActive);
    
    /**
     * Get all service categories
     * 
     * @return List of all service categories
     */
    List<ServiceCategory> getAllServiceCategories();
    
    /**
     * Get active service categories
     * 
     * @return List of active service categories
     */
    List<ServiceCategory> getActiveServiceCategories();
    
    /**
     * Get service categories by active status
     * 
     * @param isActive the active status
     * @return List of service categories with the specified status
     */
    List<ServiceCategory> getServiceCategoriesByStatus(Boolean isActive);
    
    /**
     * Search service categories by name
     * 
     * @param name the name to search for
     * @return List of service categories whose name contains the search term
     */
    List<ServiceCategory> searchServiceCategoriesByName(String name);
    
    /**
     * Search active service categories by name
     * 
     * @param name the name to search for
     * @return List of active service categories whose name contains the search term
     */
    List<ServiceCategory> searchActiveServiceCategoriesByName(String name);
    
    /**
     * Get all service categories with pagination
     * 
     * @param pageable pagination information
     * @return Page of service categories
     */
    Page<ServiceCategory> getAllServiceCategories(Pageable pageable);
    
    /**
     * Get active service categories with pagination
     * 
     * @param pageable pagination information
     * @return Page of active service categories
     */
    Page<ServiceCategory> getActiveServiceCategories(Pageable pageable);
    
    /**
     * Get service categories by status with pagination
     * 
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of service categories with the specified status
     */
    Page<ServiceCategory> getServiceCategoriesByStatus(Boolean isActive, Pageable pageable);
    
    /**
     * Check if service category name exists
     * 
     * @param name the category name to check
     * @return true if name exists, false otherwise
     */
    boolean nameExists(String name);
    
    /**
     * Count active service categories
     * 
     * @return number of active service categories
     */
    long countActiveServiceCategories();
    
    /**
     * Count service categories by status
     * 
     * @param isActive the active status
     * @return number of service categories with the specified status
     */
    long countServiceCategoriesByStatus(Boolean isActive);
    
    /**
     * Delete service category (soft delete by setting isActive to false)
     * 
     * @param id the service category ID
     * @return true if service category deleted successfully
     * @throws IllegalArgumentException if service category not found
     */
    boolean deleteServiceCategory(UUID id);
}
