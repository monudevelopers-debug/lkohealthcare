package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.repository.ServiceRepository;
import com.lucknow.healthcare.service.interfaces.ServiceService;
import com.lucknow.healthcare.service.interfaces.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Service entity operations
 * 
 * Implements business logic for service management including
 * CRUD operations, category-based queries, price filtering, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@org.springframework.stereotype.Service
@Transactional
public class ServiceServiceImpl implements ServiceService {
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    @Override
    public Service createService(Service service) {
        // Validate category exists
        if (service.getCategory() == null || service.getCategory().getId() == null) {
            throw new IllegalArgumentException("Service category is required");
        }
        
        Optional<ServiceCategory> categoryOpt = serviceCategoryService.findById(service.getCategory().getId());
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Service category not found with ID: " + service.getCategory().getId());
        }
        
        // Set default values
        service.setIsActive(true);
        
        return serviceRepository.save(service);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Service> findById(UUID id) {
        return serviceRepository.findById(id);
    }
    
    @Override
    public Service updateService(Service service) {
        if (!serviceRepository.existsById(service.getId())) {
            throw new IllegalArgumentException("Service not found with ID: " + service.getId());
        }
        
        return serviceRepository.save(service);
    }
    
    @Override
    public Service updateServiceStatus(UUID id, Boolean isActive) {
        Optional<Service> serviceOpt = serviceRepository.findById(id);
        
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found with ID: " + id);
        }
        
        Service service = serviceOpt.get();
        service.setIsActive(isActive);
        
        return serviceRepository.save(service);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> getActiveServices() {
        return serviceRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> getServicesByCategory(ServiceCategory category) {
        return serviceRepository.findByCategory(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> getServicesByCategoryId(UUID categoryId) {
        return serviceRepository.findByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> getActiveServicesByCategory(ServiceCategory category) {
        return serviceRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> getActiveServicesByCategoryId(UUID categoryId) {
        return serviceRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> searchServicesByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> searchActiveServicesByName(String name) {
        return serviceRepository.findActiveByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> findServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return serviceRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> findActiveServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return serviceRepository.findActiveByPriceRange(minPrice, maxPrice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> findServicesByDuration(Integer duration) {
        return serviceRepository.findByDuration(duration);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> findActiveServicesByDuration(Integer duration) {
        return serviceRepository.findByDurationAndIsActiveTrue(duration);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Service> findServicesByCategoryAndPriceRange(ServiceCategory category, BigDecimal minPrice, BigDecimal maxPrice) {
        return serviceRepository.findByCategoryAndPriceRange(category, minPrice, maxPrice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Service> getAllServices(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Service> getActiveServices(Pageable pageable) {
        return serviceRepository.findByIsActiveTrue(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Service> getServicesByCategory(ServiceCategory category, Pageable pageable) {
        return serviceRepository.findByCategory(category, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Service> getServicesByCategoryId(UUID categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryId(categoryId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Service> getActiveServicesByCategory(ServiceCategory category, Pageable pageable) {
        return serviceRepository.findByCategoryAndIsActiveTrue(category, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Service> getActiveServicesByCategoryId(UUID categoryId, Pageable pageable) {
        return serviceRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countServicesByCategory(ServiceCategory category) {
        return serviceRepository.countByCategory(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveServicesByCategory(ServiceCategory category) {
        return serviceRepository.countByCategoryAndIsActiveTrue(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countServicesByStatus(Boolean isActive) {
        return serviceRepository.countByIsActive(isActive);
    }
    
    @Override
    public boolean deleteService(UUID id) {
        Optional<Service> serviceOpt = serviceRepository.findById(id);
        
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found with ID: " + id);
        }
        
        Service service = serviceOpt.get();
        service.setIsActive(false);
        
        serviceRepository.save(service);
        return true;
    }
}
