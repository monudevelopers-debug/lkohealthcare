package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import com.lucknow.healthcare.repository.ProviderRepository;
import com.lucknow.healthcare.repository.ServiceRepository;
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
 * Unit tests for ProviderServiceImpl
 * 
 * Tests all business logic methods for provider management including
 * CRUD operations, availability management, rating updates, and service assignments.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ProviderServiceImpl providerService;

    private Provider testProvider;
    private Service testService;

    @BeforeEach
    void setUp() {
        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Home Nursing");
        testService.setPrice(new BigDecimal("500.00"));
        testService.setDuration(60);

        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setName("Dr. John Smith");
        testProvider.setEmail("john.smith@example.com");
        testProvider.setPhone("+91-9876543210");
        testProvider.setAddress("123 Main Street, Lucknow");
        testProvider.setQualifications("MBBS, MD");
        testProvider.setExperience(5);
        testProvider.setRating(4.5);
        testProvider.setReviewCount(25);
        testProvider.setIsAvailable(true);
        testProvider.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        testProvider.setServices(Arrays.asList(testService));
        testProvider.setCreatedAt(LocalDateTime.now());
        testProvider.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateProvider_Success() {
        // Given
        Provider newProvider = new Provider();
        newProvider.setName("Dr. Jane Doe");
        newProvider.setEmail("jane.doe@example.com");
        newProvider.setPhone("+91-9876543211");
        newProvider.setQualifications("BDS, MDS");
        newProvider.setExperience(3);

        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Provider result = providerService.createProvider(newProvider);

        // Then
        assertNotNull(result);
        assertEquals(testProvider.getId(), result.getId());
        assertEquals(testProvider.getName(), result.getName());
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testCreateProvider_WithNullName_ThrowsException() {
        // Given
        Provider newProvider = new Provider();
        newProvider.setName(null);
        newProvider.setEmail("test@example.com");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            providerService.createProvider(newProvider);
        });
    }

    @Test
    void testCreateProvider_WithInvalidEmail_ThrowsException() {
        // Given
        Provider newProvider = new Provider();
        newProvider.setName("Test Provider");
        newProvider.setEmail("invalid-email");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            providerService.createProvider(newProvider);
        });
    }

    @Test
    void testCreateProvider_WithNegativeExperience_ThrowsException() {
        // Given
        Provider newProvider = new Provider();
        newProvider.setName("Test Provider");
        newProvider.setEmail("test@example.com");
        newProvider.setExperience(-1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            providerService.createProvider(newProvider);
        });
    }

    @Test
    void testGetProviderById_Success() {
        // Given
        UUID providerId = testProvider.getId();
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));

        // When
        Optional<Provider> result = providerService.getProviderById(providerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProvider.getId(), result.get().getId());
        assertEquals(testProvider.getName(), result.get().getName());
        verify(providerRepository, times(1)).findById(providerId);
    }

    @Test
    void testGetProviderById_NotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.getProviderById(providerId);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
    }

    @Test
    void testGetAllProviders_Success() {
        // Given
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findAll()).thenReturn(providers);

        // When
        List<Provider> result = providerService.getAllProviders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findAll();
    }

    @Test
    void testGetAvailableProviders_Success() {
        // Given
        List<Provider> availableProviders = Arrays.asList(testProvider);
        when(providerRepository.findByIsAvailableTrue()).thenReturn(availableProviders);

        // When
        List<Provider> result = providerService.getAvailableProviders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAvailable());
        verify(providerRepository, times(1)).findByIsAvailableTrue();
    }

    @Test
    void testGetProvidersByService_Success() {
        // Given
        UUID serviceId = testService.getId();
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findByServicesId(serviceId)).thenReturn(providers);

        // When
        List<Provider> result = providerService.getProvidersByService(serviceId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findByServicesId(serviceId);
    }

    @Test
    void testGetProvidersByRating_Success() {
        // Given
        double minRating = 4.0;
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findByRatingGreaterThanEqual(minRating)).thenReturn(providers);

        // When
        List<Provider> result = providerService.getProvidersByRating(minRating);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findByRatingGreaterThanEqual(minRating);
    }

    @Test
    void testGetProvidersByExperience_Success() {
        // Given
        int minExperience = 3;
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findByExperienceGreaterThanEqual(minExperience)).thenReturn(providers);

        // When
        List<Provider> result = providerService.getProvidersByExperience(minExperience);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findByExperienceGreaterThanEqual(minExperience);
    }

    @Test
    void testSearchProviders_Success() {
        // Given
        String searchTerm = "john";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findByNameContainingIgnoreCaseOrQualificationsContainingIgnoreCase(
            searchTerm, searchTerm)).thenReturn(providers);

        // When
        List<Provider> result = providerService.searchProviders(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1))
            .findByNameContainingIgnoreCaseOrQualificationsContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Test
    void testUpdateProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        Provider updatedProvider = new Provider();
        updatedProvider.setName("Dr. John Smith Updated");
        updatedProvider.setPhone("+91-9876543212");
        updatedProvider.setExperience(6);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Optional<Provider> result = providerService.updateProvider(providerId, updatedProvider);

        // Then
        assertTrue(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testUpdateProvider_NotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        Provider updatedProvider = new Provider();
        updatedProvider.setName("Updated Provider");

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.updateProvider(providerId, updatedProvider);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, never()).save(any(Provider.class));
    }

    @Test
    void testDeleteProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        when(providerRepository.existsById(providerId)).thenReturn(true);

        // When
        boolean result = providerService.deleteProvider(providerId);

        // Then
        assertTrue(result);
        verify(providerRepository, times(1)).existsById(providerId);
        verify(providerRepository, times(1)).deleteById(providerId);
    }

    @Test
    void testDeleteProvider_NotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerRepository.existsById(providerId)).thenReturn(false);

        // When
        boolean result = providerService.deleteProvider(providerId);

        // Then
        assertFalse(result);
        verify(providerRepository, times(1)).existsById(providerId);
        verify(providerRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testToggleProviderAvailability_Success() {
        // Given
        UUID providerId = testProvider.getId();
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Optional<Provider> result = providerService.toggleProviderAvailability(providerId);

        // Then
        assertTrue(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testToggleProviderAvailability_NotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.toggleProviderAvailability(providerId);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, never()).save(any(Provider.class));
    }

    @Test
    void testUpdateProviderRating_Success() {
        // Given
        UUID providerId = testProvider.getId();
        double newRating = 4.8;
        int newReviewCount = 30;

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Optional<Provider> result = providerService.updateProviderRating(providerId, newRating, newReviewCount);

        // Then
        assertTrue(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testUpdateProviderRating_NotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        double newRating = 4.8;
        int newReviewCount = 30;

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.updateProviderRating(providerId, newRating, newReviewCount);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, never()).save(any(Provider.class));
    }

    @Test
    void testAddServiceToProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        UUID serviceId = testService.getId();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));
        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Optional<Provider> result = providerService.addServiceToProvider(providerId, serviceId);

        // Then
        assertTrue(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testAddServiceToProvider_ProviderNotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        UUID serviceId = testService.getId();

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.addServiceToProvider(providerId, serviceId);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(serviceRepository, never()).findById(any(UUID.class));
        verify(providerRepository, never()).save(any(Provider.class));
    }

    @Test
    void testAddServiceToProvider_ServiceNotFound() {
        // Given
        UUID providerId = testProvider.getId();
        UUID serviceId = UUID.randomUUID();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.addServiceToProvider(providerId, serviceId);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(serviceRepository, times(1)).findById(serviceId);
        verify(providerRepository, never()).save(any(Provider.class));
    }

    @Test
    void testRemoveServiceFromProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        UUID serviceId = testService.getId();

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Optional<Provider> result = providerService.removeServiceFromProvider(providerId, serviceId);

        // Then
        assertTrue(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testRemoveServiceFromProvider_NotFound() {
        // Given
        UUID providerId = UUID.randomUUID();
        UUID serviceId = testService.getId();

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Provider> result = providerService.removeServiceFromProvider(providerId, serviceId);

        // Then
        assertFalse(result.isPresent());
        verify(providerRepository, times(1)).findById(providerId);
        verify(providerRepository, never()).save(any(Provider.class));
    }

    @Test
    void testGetProvidersWithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Provider> providers = Arrays.asList(testProvider);
        Page<Provider> providerPage = new PageImpl<>(providers, pageable, 1);
        
        when(providerRepository.findAll(pageable)).thenReturn(providerPage);

        // When
        Page<Provider> result = providerService.getProvidersWithPagination(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(providerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetProviderCount_Success() {
        // Given
        long expectedCount = 15L;
        when(providerRepository.count()).thenReturn(expectedCount);

        // When
        long result = providerService.getProviderCount();

        // Then
        assertEquals(expectedCount, result);
        verify(providerRepository, times(1)).count();
    }

    @Test
    void testGetAvailableProviderCount_Success() {
        // Given
        long expectedCount = 12L;
        when(providerRepository.countByIsAvailableTrue()).thenReturn(expectedCount);

        // When
        long result = providerService.getAvailableProviderCount();

        // Then
        assertEquals(expectedCount, result);
        verify(providerRepository, times(1)).countByIsAvailableTrue();
    }

    @Test
    void testGetTopRatedProviders_Success() {
        // Given
        int limit = 5;
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findTopRatedProviders(limit)).thenReturn(providers);

        // When
        List<Provider> result = providerService.getTopRatedProviders(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findTopRatedProviders(limit);
    }

    @Test
    void testGetProvidersByLocation_Success() {
        // Given
        String location = "Lucknow";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findByAddressContainingIgnoreCase(location)).thenReturn(providers);

        // When
        List<Provider> result = providerService.getProvidersByLocation(location);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findByAddressContainingIgnoreCase(location);
    }

    @Test
    void testGetProvidersByQualification_Success() {
        // Given
        String qualification = "MBBS";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findByQualificationsContainingIgnoreCase(qualification)).thenReturn(providers);

        // When
        List<Provider> result = providerService.getProvidersByQualification(qualification);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(providerRepository, times(1)).findByQualificationsContainingIgnoreCase(qualification);
    }
}
