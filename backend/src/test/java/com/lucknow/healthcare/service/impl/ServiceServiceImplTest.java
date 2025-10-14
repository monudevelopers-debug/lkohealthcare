package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.repository.ServiceRepository;
import com.lucknow.healthcare.repository.ServiceCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ServiceServiceImpl
 * 
 * Tests all business logic methods for service management including
 * CRUD operations, search functionality, pricing, and category filtering.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ServiceServiceImplTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceCategoryRepository serviceCategoryRepository;

    @InjectMocks
    private ServiceServiceImpl serviceService;

    private Service testService;
    private ServiceCategory testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new ServiceCategory();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("Nursing Care");
        testCategory.setDescription("Professional nursing services");
        testCategory.setIsActive(true);

        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Home Nursing");
        testService.setDescription("Professional nursing care at home");
        testService.setPrice(new BigDecimal("500.00"));
        testService.setDuration(60);
        testService.setCategory(testCategory);
        testService.setIsActive(true);
        testService.setRating(4.5);
        testService.setReviewCount(10);
        testService.setCreatedAt(LocalDateTime.now());
        testService.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateService_Success() {
        // Given
        Service newService = new Service();
        newService.setName("Physiotherapy");
        newService.setDescription("Physical therapy services");
        newService.setPrice(new BigDecimal("800.00"));
        newService.setDuration(45);
        newService.setCategory(testCategory);

        when(serviceCategoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // When
        Service result = serviceService.createService(newService);

        // Then
        assertNotNull(result);
        assertEquals(testService.getId(), result.getId());
        assertEquals(testService.getName(), result.getName());
        verify(serviceCategoryRepository, times(1)).findById(testCategory.getId());
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void testCreateService_WithInvalidCategory_ThrowsException() {
        // Given
        Service newService = new Service();
        newService.setName("Test Service");
        newService.setCategory(testCategory);

        when(serviceCategoryRepository.findById(testCategory.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.createService(newService);
        });
    }

    @Test
    void testCreateService_WithNullName_ThrowsException() {
        // Given
        Service newService = new Service();
        newService.setName(null);
        newService.setCategory(testCategory);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.createService(newService);
        });
    }

    @Test
    void testCreateService_WithNegativePrice_ThrowsException() {
        // Given
        Service newService = new Service();
        newService.setName("Test Service");
        newService.setPrice(new BigDecimal("-100.00"));
        newService.setCategory(testCategory);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.createService(newService);
        });
    }

    @Test
    void testGetServiceById_Success() {
        // Given
        UUID serviceId = testService.getId();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // When
        Optional<Service> result = serviceService.getServiceById(serviceId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testService.getId(), result.get().getId());
        assertEquals(testService.getName(), result.get().getName());
        verify(serviceRepository, times(1)).findById(serviceId);
    }

    @Test
    void testGetServiceById_NotFound() {
        // Given
        UUID serviceId = UUID.randomUUID();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // When
        Optional<Service> result = serviceService.getServiceById(serviceId);

        // Then
        assertFalse(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
    }

    @Test
    void testGetAllServices_Success() {
        // Given
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findAll()).thenReturn(services);

        // When
        List<Service> result = serviceService.getAllServices();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    void testGetActiveServices_Success() {
        // Given
        List<Service> activeServices = Arrays.asList(testService);
        when(serviceRepository.findByIsActiveTrue()).thenReturn(activeServices);

        // When
        List<Service> result = serviceService.getActiveServices();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(serviceRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetServicesByCategory_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByCategoryId(categoryId)).thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByCategory(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findByCategoryId(categoryId);
    }

    @Test
    void testGetServicesByPriceRange_Success() {
        // Given
        BigDecimal minPrice = new BigDecimal("400.00");
        BigDecimal maxPrice = new BigDecimal("600.00");
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    void testGetServicesByPriceRange_WithInvalidRange_ThrowsException() {
        // Given
        BigDecimal minPrice = new BigDecimal("600.00");
        BigDecimal maxPrice = new BigDecimal("400.00");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceService.getServicesByPriceRange(minPrice, maxPrice);
        });
    }

    @Test
    void testSearchServices_Success() {
        // Given
        String searchTerm = "nursing";
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            searchTerm, searchTerm)).thenReturn(services);

        // When
        List<Service> result = serviceService.searchServices(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1))
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Test
    void testUpdateService_Success() {
        // Given
        UUID serviceId = testService.getId();
        Service updatedService = new Service();
        updatedService.setName("Updated Home Nursing");
        updatedService.setDescription("Updated description");
        updatedService.setPrice(new BigDecimal("600.00"));
        updatedService.setDuration(90);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // When
        Optional<Service> result = serviceService.updateService(serviceId, updatedService);

        // Then
        assertTrue(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void testUpdateService_NotFound() {
        // Given
        UUID serviceId = UUID.randomUUID();
        Service updatedService = new Service();
        updatedService.setName("Updated Service");

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // When
        Optional<Service> result = serviceService.updateService(serviceId, updatedService);

        // Then
        assertFalse(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void testDeleteService_Success() {
        // Given
        UUID serviceId = testService.getId();
        when(serviceRepository.existsById(serviceId)).thenReturn(true);

        // When
        boolean result = serviceService.deleteService(serviceId);

        // Then
        assertTrue(result);
        verify(serviceRepository, times(1)).existsById(serviceId);
        verify(serviceRepository, times(1)).deleteById(serviceId);
    }

    @Test
    void testDeleteService_NotFound() {
        // Given
        UUID serviceId = UUID.randomUUID();
        when(serviceRepository.existsById(serviceId)).thenReturn(false);

        // When
        boolean result = serviceService.deleteService(serviceId);

        // Then
        assertFalse(result);
        verify(serviceRepository, times(1)).existsById(serviceId);
        verify(serviceRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testToggleServiceStatus_Success() {
        // Given
        UUID serviceId = testService.getId();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // When
        Optional<Service> result = serviceService.toggleServiceStatus(serviceId);

        // Then
        assertTrue(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void testToggleServiceStatus_NotFound() {
        // Given
        UUID serviceId = UUID.randomUUID();
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // When
        Optional<Service> result = serviceService.toggleServiceStatus(serviceId);

        // Then
        assertFalse(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void testGetServicesWithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Service> services = Arrays.asList(testService);
        Page<Service> servicePage = new PageImpl<>(services, pageable, 1);
        
        when(serviceRepository.findAll(pageable)).thenReturn(servicePage);

        // When
        Page<Service> result = serviceService.getServicesWithPagination(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(serviceRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetServicesByRating_Success() {
        // Given
        double minRating = 4.0;
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByRatingGreaterThanEqual(minRating)).thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByRating(minRating);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findByRatingGreaterThanEqual(minRating);
    }

    @Test
    void testGetServicesByDuration_Success() {
        // Given
        int maxDuration = 90;
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByDurationLessThanEqual(maxDuration)).thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByDuration(maxDuration);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findByDurationLessThanEqual(maxDuration);
    }

    @Test
    void testGetServiceCount_Success() {
        // Given
        long expectedCount = 10L;
        when(serviceRepository.count()).thenReturn(expectedCount);

        // When
        long result = serviceService.getServiceCount();

        // Then
        assertEquals(expectedCount, result);
        verify(serviceRepository, times(1)).count();
    }

    @Test
    void testGetActiveServiceCount_Success() {
        // Given
        long expectedCount = 8L;
        when(serviceRepository.countByIsActiveTrue()).thenReturn(expectedCount);

        // When
        long result = serviceService.getActiveServiceCount();

        // Then
        assertEquals(expectedCount, result);
        verify(serviceRepository, times(1)).countByIsActiveTrue();
    }

    @Test
    void testGetServicesByCategoryAndPrice_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        BigDecimal maxPrice = new BigDecimal("600.00");
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByCategoryIdAndPriceLessThanEqual(categoryId, maxPrice))
            .thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByCategoryAndPrice(categoryId, maxPrice);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1))
            .findByCategoryIdAndPriceLessThanEqual(categoryId, maxPrice);
    }

    @Test
    void testUpdateServiceRating_Success() {
        // Given
        UUID serviceId = testService.getId();
        double newRating = 4.8;
        int newReviewCount = 15;

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any(Service.class))).thenReturn(testService);

        // When
        Optional<Service> result = serviceService.updateServiceRating(serviceId, newRating, newReviewCount);

        // Then
        assertTrue(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void testUpdateServiceRating_NotFound() {
        // Given
        UUID serviceId = UUID.randomUUID();
        double newRating = 4.8;
        int newReviewCount = 15;

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // When
        Optional<Service> result = serviceService.updateServiceRating(serviceId, newRating, newReviewCount);

        // Then
        assertFalse(result.isPresent());
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void testGetTopRatedServices_Success() {
        // Given
        int limit = 5;
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findTopRatedServices(limit)).thenReturn(services);

        // When
        List<Service> result = serviceService.getTopRatedServices(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findTopRatedServices(limit);
    }

    @Test
    void testGetServicesByCategoryAndActive_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByCategoryIdAndIsActiveTrue(categoryId)).thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByCategoryAndActive(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findByCategoryIdAndIsActiveTrue(categoryId);
    }
}
