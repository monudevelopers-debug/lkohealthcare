package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Service entity operations
 * 
 * Defines business logic methods for service management including
 * CRUD operations, category-based queries, price filtering, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface ServiceService {
    
    /**
     * Create a new service
     * 
     * @param service the service to create
     * @return the created service
     * @throws IllegalArgumentException if category not found or service name already exists in category
     */
    Service createService(Service service);
    
    /**
     * Find service by ID
     * 
     * @param id the service ID
     * @return Optional containing the service if found
     */
    Optional<Service> findById(UUID id);
    
    /**
     * Update service
     * 
     * @param service the service with updated information
     * @return the updated service
     * @throws IllegalArgumentException if service not found
     */
    Service updateService(Service service);
    
    /**
     * Update service status
     * 
     * @param id the service ID
     * @param isActive the new active status
     * @return the updated service
     * @throws IllegalArgumentException if service not found
     */
    Service updateServiceStatus(UUID id, Boolean isActive);
    
    /**
     * Get all services
     * 
     * @return List of all services
     */
    List<Service> getAllServices();
    
    /**
     * Get active services
     * 
     * @return List of active services
     */
    List<Service> getActiveServices();
    
    /**
     * Get services by category
     * 
     * @param category the service category
     * @return List of services in the specified category
     */
    List<Service> getServicesByCategory(ServiceCategory category);
    
    /**
     * Get services by category ID
     * 
     * @param categoryId the category ID
     * @return List of services in the specified category
     */
    List<Service> getServicesByCategoryId(UUID categoryId);
    
    /**
     * Get active services by category
     * 
     * @param category the service category
     * @return List of active services in the specified category
     */
    List<Service> getActiveServicesByCategory(ServiceCategory category);
    
    /**
     * Get active services by category ID
     * 
     * @param categoryId the category ID
     * @return List of active services in the specified category
     */
    List<Service> getActiveServicesByCategoryId(UUID categoryId);
    
    /**
     * Search services by name
     * 
     * @param name the name to search for
     * @return List of services whose name contains the search term
     */
    List<Service> searchServicesByName(String name);
    
    /**
     * Search active services by name
     * 
     * @param name the name to search for
     * @return List of active services whose name contains the search term
     */
    List<Service> searchActiveServicesByName(String name);
    
    /**
     * Find services by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of services within the price range
     */
    List<Service> findServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find active services by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of active services within the price range
     */
    List<Service> findActiveServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find services by duration
     * 
     * @param duration the duration to filter by
     * @return List of services with the specified duration
     */
    List<Service> findServicesByDuration(Integer duration);
    
    /**
     * Find active services by duration
     * 
     * @param duration the duration to filter by
     * @return List of active services with the specified duration
     */
    List<Service> findActiveServicesByDuration(Integer duration);
    
    /**
     * Find services by category and price range
     * 
     * @param category the service category
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of services in the category within the price range
     */
    List<Service> findServicesByCategoryAndPriceRange(ServiceCategory category, BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get all services with pagination
     * 
     * @param pageable pagination information
     * @return Page of services
     */
    Page<Service> getAllServices(Pageable pageable);
    
    /**
     * Get active services with pagination
     * 
     * @param pageable pagination information
     * @return Page of active services
     */
    Page<Service> getActiveServices(Pageable pageable);
    
    /**
     * Get services by category with pagination
     * 
     * @param category the service category
     * @param pageable pagination information
     * @return Page of services in the specified category
     */
    Page<Service> getServicesByCategory(ServiceCategory category, Pageable pageable);
    
    /**
     * Get services by category ID with pagination
     * 
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return Page of services in the specified category
     */
    Page<Service> getServicesByCategoryId(UUID categoryId, Pageable pageable);
    
    /**
     * Get active services by category with pagination
     * 
     * @param category the service category
     * @param pageable pagination information
     * @return Page of active services in the specified category
     */
    Page<Service> getActiveServicesByCategory(ServiceCategory category, Pageable pageable);
    
    /**
     * Get active services by category ID with pagination
     * 
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return Page of active services in the specified category
     */
    Page<Service> getActiveServicesByCategoryId(UUID categoryId, Pageable pageable);
    
    /**
     * Count services by category
     * 
     * @param category the service category
     * @return number of services in the specified category
     */
    long countServicesByCategory(ServiceCategory category);
    
    /**
     * Count active services by category
     * 
     * @param category the service category
     * @return number of active services in the specified category
     */
    long countActiveServicesByCategory(ServiceCategory category);
    
    /**
     * Count services by active status
     * 
     * @param isActive the active status
     * @return number of services with the specified status
     */
    long countServicesByStatus(Boolean isActive);
    
    /**
     * Delete service (soft delete by setting isActive to false)
     * 
     * @param id the service ID
     * @return true if service deleted successfully
     * @throws IllegalArgumentException if service not found
     */
    boolean deleteService(UUID id);
}
