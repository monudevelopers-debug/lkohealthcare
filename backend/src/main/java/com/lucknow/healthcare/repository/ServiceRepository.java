package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Service entity
 * 
 * Provides data access methods for service management including
 * category-based queries, price filtering, and availability checks.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    
    /**
     * Find services by category
     * 
     * @param category the service category to filter by
     * @return List of services in the specified category
     */
    List<Service> findByCategory(ServiceCategory category);
    
    /**
     * Find services by category ID
     * 
     * @param categoryId the category ID to filter by
     * @return List of services in the specified category
     */
    List<Service> findByCategoryId(UUID categoryId);
    
    /**
     * Find active services
     * 
     * @return List of active services
     */
    List<Service> findByIsActiveTrue();
    
    /**
     * Find services by active status
     * 
     * @param isActive the active status to filter by
     * @return List of services with the specified status
     */
    List<Service> findByIsActive(Boolean isActive);
    
    /**
     * Find active services by category
     * 
     * @param category the service category to filter by
     * @return List of active services in the specified category
     */
    List<Service> findByCategoryAndIsActiveTrue(ServiceCategory category);
    
    /**
     * Find active services by category ID
     * 
     * @param categoryId the category ID to filter by
     * @return List of active services in the specified category
     */
    List<Service> findByCategoryIdAndIsActiveTrue(UUID categoryId);
    
    /**
     * Find services by name containing (case-insensitive)
     * 
     * @param name the name to search for
     * @return List of services whose name contains the search term
     */
    @Query("SELECT s FROM Service s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Service> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find active services by name containing (case-insensitive)
     * 
     * @param name the name to search for
     * @return List of active services whose name contains the search term
     */
    @Query("SELECT s FROM Service s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.isActive = true")
    List<Service> findActiveByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find services by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of services within the price range
     */
    @Query("SELECT s FROM Service s WHERE s.price BETWEEN :minPrice AND :maxPrice")
    List<Service> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find active services by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of active services within the price range
     */
    @Query("SELECT s FROM Service s WHERE s.price BETWEEN :minPrice AND :maxPrice AND s.isActive = true")
    List<Service> findActiveByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find services by duration
     * 
     * @param duration the duration to filter by
     * @return List of services with the specified duration
     */
    List<Service> findByDuration(Integer duration);
    
    /**
     * Find active services by duration
     * 
     * @param duration the duration to filter by
     * @return List of active services with the specified duration
     */
    List<Service> findByDurationAndIsActiveTrue(Integer duration);
    
    /**
     * Find services by category and price range
     * 
     * @param category the service category to filter by
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of services in the category within the price range
     */
    @Query("SELECT s FROM Service s WHERE s.category = :category AND s.price BETWEEN :minPrice AND :maxPrice")
    List<Service> findByCategoryAndPriceRange(@Param("category") ServiceCategory category, 
                                            @Param("minPrice") BigDecimal minPrice, 
                                            @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Count services by category
     * 
     * @param category the service category to count
     * @return number of services in the specified category
     */
    long countByCategory(ServiceCategory category);
    
    /**
     * Count active services by category
     * 
     * @param category the service category to count
     * @return number of active services in the specified category
     */
    long countByCategoryAndIsActiveTrue(ServiceCategory category);
    
    /**
     * Count services by active status
     * 
     * @param isActive the active status to count
     * @return number of services with the specified status
     */
    long countByIsActive(Boolean isActive);
    
    // Pageable methods
    Page<Service> findByIsActiveTrue(Pageable pageable);
    Page<Service> findByCategory(ServiceCategory category, Pageable pageable);
    Page<Service> findByCategoryId(UUID categoryId, Pageable pageable);
    Page<Service> findByCategoryAndIsActiveTrue(ServiceCategory category, Pageable pageable);
    Page<Service> findByCategoryIdAndIsActiveTrue(UUID categoryId, Pageable pageable);
}
