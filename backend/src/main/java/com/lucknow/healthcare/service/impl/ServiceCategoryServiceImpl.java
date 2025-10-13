package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.repository.ServiceCategoryRepository;
import com.lucknow.healthcare.service.interfaces.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for ServiceCategory entity operations
 * 
 * Implements business logic for service category management including
 * CRUD operations, active category queries, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ServiceCategoryServiceImpl implements ServiceCategoryService {
    
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    
    @Override
    public ServiceCategory createServiceCategory(ServiceCategory serviceCategory) {
        // Check if name already exists
        if (nameExists(serviceCategory.getName())) {
            throw new IllegalArgumentException("Service category name already exists: " + serviceCategory.getName());
        }
        
        // Set default values
        serviceCategory.setIsActive(true);
        
        return serviceCategoryRepository.save(serviceCategory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceCategory> findById(UUID id) {
        return serviceCategoryRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceCategory> findByName(String name) {
        return serviceCategoryRepository.findByName(name);
    }
    
    @Override
    public ServiceCategory updateServiceCategory(ServiceCategory serviceCategory) {
        if (!serviceCategoryRepository.existsById(serviceCategory.getId())) {
            throw new IllegalArgumentException("Service category not found with ID: " + serviceCategory.getId());
        }
        
        return serviceCategoryRepository.save(serviceCategory);
    }
    
    @Override
    public ServiceCategory updateServiceCategoryStatus(UUID id, Boolean isActive) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryRepository.findById(id);
        
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Service category not found with ID: " + id);
        }
        
        ServiceCategory serviceCategory = categoryOpt.get();
        serviceCategory.setIsActive(isActive);
        
        return serviceCategoryRepository.save(serviceCategory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> getAllServiceCategories() {
        return serviceCategoryRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> getActiveServiceCategories() {
        return serviceCategoryRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> getServiceCategoriesByStatus(Boolean isActive) {
        return serviceCategoryRepository.findByIsActive(isActive);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> searchServiceCategoriesByName(String name) {
        return serviceCategoryRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> searchActiveServiceCategoriesByName(String name) {
        return serviceCategoryRepository.findActiveByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServiceCategory> getAllServiceCategories(Pageable pageable) {
        return serviceCategoryRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServiceCategory> getActiveServiceCategories(Pageable pageable) {
        return serviceCategoryRepository.findByIsActiveTrue(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServiceCategory> getServiceCategoriesByStatus(Boolean isActive, Pageable pageable) {
        return serviceCategoryRepository.findByIsActive(isActive, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean nameExists(String name) {
        return serviceCategoryRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveServiceCategories() {
        return serviceCategoryRepository.countByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countServiceCategoriesByStatus(Boolean isActive) {
        return serviceCategoryRepository.countByIsActive(isActive);
    }
    
    @Override
    public boolean deleteServiceCategory(UUID id) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryRepository.findById(id);
        
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Service category not found with ID: " + id);
        }
        
        ServiceCategory serviceCategory = categoryOpt.get();
        serviceCategory.setIsActive(false);
        
        serviceCategoryRepository.save(serviceCategory);
        return true;
    }
}
