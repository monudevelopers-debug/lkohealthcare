package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ServiceCategory entity
 * 
 * Provides data access methods for service category management including
 * active category queries and name-based searches.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, UUID> {
    
    /**
     * Find service category by name
     * 
     * @param name the category name to search for
     * @return Optional containing the category if found
     */
    Optional<ServiceCategory> findByName(String name);
    
    /**
     * Find active service categories
     * 
     * @return List of active service categories
     */
    List<ServiceCategory> findByIsActiveTrue();
    
    /**
     * Find service categories by active status
     * 
     * @param isActive the active status to filter by
     * @return List of service categories with the specified status
     */
    List<ServiceCategory> findByIsActive(Boolean isActive);
    
    /**
     * Find service categories by name containing (case-insensitive)
     * 
     * @param name the name to search for
     * @return List of service categories whose name contains the search term
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ServiceCategory> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find active service categories by name containing (case-insensitive)
     * 
     * @param name the name to search for
     * @return List of active service categories whose name contains the search term
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%')) AND sc.isActive = true")
    List<ServiceCategory> findActiveByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Check if category name exists
     * 
     * @param name the category name to check
     * @return true if name exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Count active service categories
     * 
     * @return number of active service categories
     */
    long countByIsActiveTrue();
    
    /**
     * Count service categories by active status
     * 
     * @param isActive the active status to count
     * @return number of service categories with the specified status
     */
    long countByIsActive(Boolean isActive);
    
    // Pageable methods
    Page<ServiceCategory> findByIsActiveTrue(Pageable pageable);
    Page<ServiceCategory> findByIsActive(Boolean isActive, Pageable pageable);
}
