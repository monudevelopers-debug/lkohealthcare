package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.service.interfaces.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for ServiceCategory entity operations
 * 
 * Provides REST endpoints for service category management including
 * CRUD operations, active category queries, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/service-categories")
@CrossOrigin(origins = "*")
public class ServiceCategoryController {
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    /**
     * Create a new service category
     * 
     * @param serviceCategory the service category to create
     * @return ResponseEntity containing the created service category
     */
    @PostMapping
    public ResponseEntity<ServiceCategory> createServiceCategory(@RequestBody ServiceCategory serviceCategory) {
        try {
            ServiceCategory createdCategory = serviceCategoryService.createServiceCategory(serviceCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get service category by ID
     * 
     * @param id the service category ID
     * @return ResponseEntity containing the service category if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ServiceCategory> getServiceCategoryById(@PathVariable UUID id) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryService.findById(id);
        return categoryOpt.map(category -> ResponseEntity.ok(category))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get service category by name
     * 
     * @param name the service category name
     * @return ResponseEntity containing the service category if found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ServiceCategory> getServiceCategoryByName(@PathVariable String name) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryService.findByName(name);
        return categoryOpt.map(category -> ResponseEntity.ok(category))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update service category
     * 
     * @param id the service category ID
     * @param serviceCategory the updated service category information
     * @return ResponseEntity containing the updated service category
     */
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategory> updateServiceCategory(@PathVariable UUID id, @RequestBody ServiceCategory serviceCategory) {
        try {
            serviceCategory.setId(id);
            ServiceCategory updatedCategory = serviceCategoryService.updateServiceCategory(serviceCategory);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update service category status
     * 
     * @param id the service category ID
     * @param isActive the new active status
     * @return ResponseEntity containing the updated service category
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceCategory> updateServiceCategoryStatus(@PathVariable UUID id, @RequestParam Boolean isActive) {
        try {
            ServiceCategory updatedCategory = serviceCategoryService.updateServiceCategoryStatus(id, isActive);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all service categories
     * 
     * @return ResponseEntity containing the list of service categories
     */
    @GetMapping
    public ResponseEntity<List<ServiceCategory>> getAllServiceCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getAllServiceCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get active service categories
     * 
     * @return ResponseEntity containing the list of active service categories
     */
    @GetMapping("/active")
    public ResponseEntity<List<ServiceCategory>> getActiveServiceCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getActiveServiceCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get service categories by status
     * 
     * @param isActive the active status
     * @return ResponseEntity containing the list of service categories
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<List<ServiceCategory>> getServiceCategoriesByStatus(@PathVariable Boolean isActive) {
        List<ServiceCategory> categories = serviceCategoryService.getServiceCategoriesByStatus(isActive);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Search service categories by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of service categories
     */
    @GetMapping("/search")
    public ResponseEntity<List<ServiceCategory>> searchServiceCategoriesByName(@RequestParam String name) {
        List<ServiceCategory> categories = serviceCategoryService.searchServiceCategoriesByName(name);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Search active service categories by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of active service categories
     */
    @GetMapping("/active/search")
    public ResponseEntity<List<ServiceCategory>> searchActiveServiceCategoriesByName(@RequestParam String name) {
        List<ServiceCategory> categories = serviceCategoryService.searchActiveServiceCategoriesByName(name);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get all service categories with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of service categories
     */
    @GetMapping("/page")
    public ResponseEntity<Page<ServiceCategory>> getAllServiceCategories(Pageable pageable) {
        Page<ServiceCategory> categories = serviceCategoryService.getAllServiceCategories(pageable);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get active service categories with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of active service categories
     */
    @GetMapping("/active/page")
    public ResponseEntity<Page<ServiceCategory>> getActiveServiceCategories(Pageable pageable) {
        Page<ServiceCategory> categories = serviceCategoryService.getActiveServiceCategories(pageable);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get service categories by status with pagination
     * 
     * @param isActive the active status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of service categories
     */
    @GetMapping("/status/{isActive}/page")
    public ResponseEntity<Page<ServiceCategory>> getServiceCategoriesByStatus(@PathVariable Boolean isActive, Pageable pageable) {
        Page<ServiceCategory> categories = serviceCategoryService.getServiceCategoriesByStatus(isActive, pageable);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Check if service category name exists
     * 
     * @param name the category name to check
     * @return ResponseEntity containing the existence status
     */
    @GetMapping("/name-exists/{name}")
    public ResponseEntity<Boolean> checkNameExists(@PathVariable String name) {
        boolean exists = serviceCategoryService.nameExists(name);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Count active service categories
     * 
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveServiceCategories() {
        long count = serviceCategoryService.countActiveServiceCategories();
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count service categories by status
     * 
     * @param isActive the active status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/status/{isActive}")
    public ResponseEntity<Long> countServiceCategoriesByStatus(@PathVariable Boolean isActive) {
        long count = serviceCategoryService.countServiceCategoriesByStatus(isActive);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Delete service category
     * 
     * @param id the service category ID
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCategory(@PathVariable UUID id) {
        try {
            boolean success = serviceCategoryService.deleteServiceCategory(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
