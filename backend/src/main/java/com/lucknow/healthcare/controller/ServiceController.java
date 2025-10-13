package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.service.interfaces.ServiceService;
import com.lucknow.healthcare.service.interfaces.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Service entity operations
 * 
 * Provides REST endpoints for service management including
 * CRUD operations, category-based queries, price filtering, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    /**
     * Create a new service
     * 
     * @param service the service to create
     * @return ResponseEntity containing the created service
     */
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        try {
            Service createdService = serviceService.createService(service);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get service by ID
     * 
     * @param id the service ID
     * @return ResponseEntity containing the service if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable UUID id) {
        Optional<Service> serviceOpt = serviceService.findById(id);
        return serviceOpt.map(service -> ResponseEntity.ok(service))
                        .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update service
     * 
     * @param id the service ID
     * @param service the updated service information
     * @return ResponseEntity containing the updated service
     */
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable UUID id, @RequestBody Service service) {
        try {
            service.setId(id);
            Service updatedService = serviceService.updateService(service);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update service status
     * 
     * @param id the service ID
     * @param isActive the new active status
     * @return ResponseEntity containing the updated service
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Service> updateServiceStatus(@PathVariable UUID id, @RequestParam Boolean isActive) {
        try {
            Service updatedService = serviceService.updateServiceStatus(id, isActive);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all services
     * 
     * @return ResponseEntity containing the list of services
     */
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get active services
     * 
     * @return ResponseEntity containing the list of active services
     */
    @GetMapping("/active")
    public ResponseEntity<List<Service>> getActiveServices() {
        List<Service> services = serviceService.getActiveServices();
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get services by category
     * 
     * @param categoryId the category ID
     * @return ResponseEntity containing the list of services
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable UUID categoryId) {
        List<Service> services = serviceService.getServicesByCategoryId(categoryId);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get active services by category
     * 
     * @param categoryId the category ID
     * @return ResponseEntity containing the list of active services
     */
    @GetMapping("/active/category/{categoryId}")
    public ResponseEntity<List<Service>> getActiveServicesByCategory(@PathVariable UUID categoryId) {
        List<Service> services = serviceService.getActiveServicesByCategoryId(categoryId);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Search services by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of services
     */
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServicesByName(@RequestParam String name) {
        List<Service> services = serviceService.searchServicesByName(name);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Search active services by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of active services
     */
    @GetMapping("/active/search")
    public ResponseEntity<List<Service>> searchActiveServicesByName(@RequestParam String name) {
        List<Service> services = serviceService.searchActiveServicesByName(name);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Find services by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return ResponseEntity containing the list of services
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Service>> findServicesByPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        List<Service> services = serviceService.findServicesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Find active services by price range
     * 
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return ResponseEntity containing the list of active services
     */
    @GetMapping("/active/price-range")
    public ResponseEntity<List<Service>> findActiveServicesByPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        List<Service> services = serviceService.findActiveServicesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Find services by duration
     * 
     * @param duration the duration to filter by
     * @return ResponseEntity containing the list of services
     */
    @GetMapping("/duration/{duration}")
    public ResponseEntity<List<Service>> findServicesByDuration(@PathVariable Integer duration) {
        List<Service> services = serviceService.findServicesByDuration(duration);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Find active services by duration
     * 
     * @param duration the duration to filter by
     * @return ResponseEntity containing the list of active services
     */
    @GetMapping("/active/duration/{duration}")
    public ResponseEntity<List<Service>> findActiveServicesByDuration(@PathVariable Integer duration) {
        List<Service> services = serviceService.findActiveServicesByDuration(duration);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get all services with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of services
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Service>> getAllServices(Pageable pageable) {
        Page<Service> services = serviceService.getAllServices(pageable);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get active services with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of active services
     */
    @GetMapping("/active/page")
    public ResponseEntity<Page<Service>> getActiveServices(Pageable pageable) {
        Page<Service> services = serviceService.getActiveServices(pageable);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get services by category with pagination
     * 
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return ResponseEntity containing the page of services
     */
    @GetMapping("/category/{categoryId}/page")
    public ResponseEntity<Page<Service>> getServicesByCategory(@PathVariable UUID categoryId, Pageable pageable) {
        Page<Service> services = serviceService.getServicesByCategoryId(categoryId, pageable);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Get active services by category with pagination
     * 
     * @param categoryId the category ID
     * @param pageable pagination information
     * @return ResponseEntity containing the page of active services
     */
    @GetMapping("/active/category/{categoryId}/page")
    public ResponseEntity<Page<Service>> getActiveServicesByCategory(@PathVariable UUID categoryId, Pageable pageable) {
        Page<Service> services = serviceService.getActiveServicesByCategoryId(categoryId, pageable);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Count services by category
     * 
     * @param categoryId the category ID
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/category/{categoryId}")
    public ResponseEntity<Long> countServicesByCategory(@PathVariable UUID categoryId) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        long count = serviceService.countServicesByCategory(categoryOpt.get());
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count active services by category
     * 
     * @param categoryId the category ID
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/active/category/{categoryId}")
    public ResponseEntity<Long> countActiveServicesByCategory(@PathVariable UUID categoryId) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        long count = serviceService.countActiveServicesByCategory(categoryOpt.get());
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count services by active status
     * 
     * @param isActive the active status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/status/{isActive}")
    public ResponseEntity<Long> countServicesByStatus(@PathVariable Boolean isActive) {
        long count = serviceService.countServicesByStatus(isActive);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Delete service
     * 
     * @param id the service ID
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        try {
            boolean success = serviceService.deleteService(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
