package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.ServiceCategory;
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
 * Unit tests for ServiceCategoryServiceImpl
 * 
 * Tests all business logic methods for service category management including
 * CRUD operations, search functionality, and status management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ServiceCategoryServiceImplTest {

    @Mock
    private ServiceCategoryRepository serviceCategoryRepository;

    @InjectMocks
    private ServiceCategoryServiceImpl serviceCategoryService;

    private ServiceCategory testCategory;
    private ServiceCategory inactiveCategory;

    @BeforeEach
    void setUp() {
        testCategory = new ServiceCategory();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("Nursing Care");
        testCategory.setDescription("Professional nursing services for patients");
        testCategory.setIsActive(true);
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        inactiveCategory = new ServiceCategory();
        inactiveCategory.setId(UUID.randomUUID());
        inactiveCategory.setName("Inactive Category");
        inactiveCategory.setDescription("This category is inactive");
        inactiveCategory.setIsActive(false);
        inactiveCategory.setCreatedAt(LocalDateTime.now());
        inactiveCategory.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateCategory_Success() {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("Physiotherapy");
        newCategory.setDescription("Physical therapy services");
        newCategory.setIsActive(true);

        when(serviceCategoryRepository.save(any(ServiceCategory.class))).thenReturn(testCategory);

        // When
        ServiceCategory result = serviceCategoryService.createCategory(newCategory);

        // Then
        assertNotNull(result);
        assertEquals(testCategory.getId(), result.getId());
        assertEquals(testCategory.getName(), result.getName());
        verify(serviceCategoryRepository, times(1)).save(any(ServiceCategory.class));
    }

    @Test
    void testCreateCategory_WithNullName_ThrowsException() {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName(null);
        newCategory.setDescription("Test description");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceCategoryService.createCategory(newCategory);
        });
    }

    @Test
    void testCreateCategory_WithEmptyName_ThrowsException() {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("");
        newCategory.setDescription("Test description");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceCategoryService.createCategory(newCategory);
        });
    }

    @Test
    void testGetCategoryById_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When
        Optional<ServiceCategory> result = serviceCategoryService.getCategoryById(categoryId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCategory.getId(), result.get().getId());
        assertEquals(testCategory.getName(), result.get().getName());
        verify(serviceCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Optional<ServiceCategory> result = serviceCategoryService.getCategoryById(categoryId);

        // Then
        assertFalse(result.isPresent());
        verify(serviceCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetAllCategories_Success() {
        // Given
        List<ServiceCategory> categories = Arrays.asList(testCategory, inactiveCategory);
        when(serviceCategoryRepository.findAll()).thenReturn(categories);

        // When
        List<ServiceCategory> result = serviceCategoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(serviceCategoryRepository, times(1)).findAll();
    }

    @Test
    void testGetActiveCategories_Success() {
        // Given
        List<ServiceCategory> activeCategories = Arrays.asList(testCategory);
        when(serviceCategoryRepository.findByIsActiveTrue()).thenReturn(activeCategories);

        // When
        List<ServiceCategory> result = serviceCategoryService.getActiveCategories();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        verify(serviceCategoryRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    void testGetCategoriesByName_Success() {
        // Given
        String categoryName = "Nursing";
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        when(serviceCategoryRepository.findByNameContainingIgnoreCase(categoryName)).thenReturn(categories);

        // When
        List<ServiceCategory> result = serviceCategoryService.getCategoriesByName(categoryName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceCategoryRepository, times(1)).findByNameContainingIgnoreCase(categoryName);
    }

    @Test
    void testUpdateCategory_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName("Updated Nursing Care");
        updatedCategory.setDescription("Updated description");
        updatedCategory.setIsActive(true);

        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(serviceCategoryRepository.save(any(ServiceCategory.class))).thenReturn(testCategory);

        // When
        Optional<ServiceCategory> result = serviceCategoryService.updateCategory(categoryId, updatedCategory);

        // Then
        assertTrue(result.isPresent());
        verify(serviceCategoryRepository, times(1)).findById(categoryId);
        verify(serviceCategoryRepository, times(1)).save(any(ServiceCategory.class));
    }

    @Test
    void testUpdateCategory_NotFound() {
        // Given
        UUID categoryId = UUID.randomUUID();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName("Updated Category");

        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Optional<ServiceCategory> result = serviceCategoryService.updateCategory(categoryId, updatedCategory);

        // Then
        assertFalse(result.isPresent());
        verify(serviceCategoryRepository, times(1)).findById(categoryId);
        verify(serviceCategoryRepository, never()).save(any(ServiceCategory.class));
    }

    @Test
    void testDeleteCategory_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        when(serviceCategoryRepository.existsById(categoryId)).thenReturn(true);

        // When
        boolean result = serviceCategoryService.deleteCategory(categoryId);

        // Then
        assertTrue(result);
        verify(serviceCategoryRepository, times(1)).existsById(categoryId);
        verify(serviceCategoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testDeleteCategory_NotFound() {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(serviceCategoryRepository.existsById(categoryId)).thenReturn(false);

        // When
        boolean result = serviceCategoryService.deleteCategory(categoryId);

        // Then
        assertFalse(result);
        verify(serviceCategoryRepository, times(1)).existsById(categoryId);
        verify(serviceCategoryRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testToggleCategoryStatus_Success() {
        // Given
        UUID categoryId = testCategory.getId();
        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(serviceCategoryRepository.save(any(ServiceCategory.class))).thenReturn(testCategory);

        // When
        Optional<ServiceCategory> result = serviceCategoryService.toggleCategoryStatus(categoryId);

        // Then
        assertTrue(result.isPresent());
        verify(serviceCategoryRepository, times(1)).findById(categoryId);
        verify(serviceCategoryRepository, times(1)).save(any(ServiceCategory.class));
    }

    @Test
    void testToggleCategoryStatus_NotFound() {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Optional<ServiceCategory> result = serviceCategoryService.toggleCategoryStatus(categoryId);

        // Then
        assertFalse(result.isPresent());
        verify(serviceCategoryRepository, times(1)).findById(categoryId);
        verify(serviceCategoryRepository, never()).save(any(ServiceCategory.class));
    }

    @Test
    void testGetCategoriesWithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        Page<ServiceCategory> categoryPage = new PageImpl<>(categories, pageable, 1);
        
        when(serviceCategoryRepository.findAll(pageable)).thenReturn(categoryPage);

        // When
        Page<ServiceCategory> result = serviceCategoryService.getCategoriesWithPagination(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(serviceCategoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void testSearchCategories_Success() {
        // Given
        String searchTerm = "nursing";
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        when(serviceCategoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            searchTerm, searchTerm)).thenReturn(categories);

        // When
        List<ServiceCategory> result = serviceCategoryService.searchCategories(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceCategoryRepository, times(1))
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Test
    void testGetCategoryCount_Success() {
        // Given
        long expectedCount = 5L;
        when(serviceCategoryRepository.count()).thenReturn(expectedCount);

        // When
        long result = serviceCategoryService.getCategoryCount();

        // Then
        assertEquals(expectedCount, result);
        verify(serviceCategoryRepository, times(1)).count();
    }

    @Test
    void testGetActiveCategoryCount_Success() {
        // Given
        long expectedCount = 3L;
        when(serviceCategoryRepository.countByIsActiveTrue()).thenReturn(expectedCount);

        // When
        long result = serviceCategoryService.getActiveCategoryCount();

        // Then
        assertEquals(expectedCount, result);
        verify(serviceCategoryRepository, times(1)).countByIsActiveTrue();
    }

    @Test
    void testCreateCategory_WithDuplicateName_ThrowsException() {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("Nursing Care");
        newCategory.setDescription("Test description");

        when(serviceCategoryRepository.findByNameIgnoreCase("Nursing Care"))
            .thenReturn(Optional.of(testCategory));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceCategoryService.createCategory(newCategory);
        });
    }

    @Test
    void testUpdateCategory_WithNullName_ThrowsException() {
        // Given
        UUID categoryId = testCategory.getId();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName(null);

        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            serviceCategoryService.updateCategory(categoryId, updatedCategory);
        });
    }

    @Test
    void testSearchCategories_WithEmptySearchTerm_ReturnsAllCategories() {
        // Given
        String searchTerm = "";
        List<ServiceCategory> categories = Arrays.asList(testCategory, inactiveCategory);
        when(serviceCategoryRepository.findAll()).thenReturn(categories);

        // When
        List<ServiceCategory> result = serviceCategoryService.searchCategories(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(serviceCategoryRepository, times(1)).findAll();
    }
}
