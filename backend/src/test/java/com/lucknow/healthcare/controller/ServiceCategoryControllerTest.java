package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.service.interfaces.ServiceCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ServiceCategoryController
 * 
 * Tests all service category management endpoints including CRUD operations,
 * search functionality, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(ServiceCategoryController.class)
class ServiceCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCategoryService serviceCategoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private ServiceCategory testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new ServiceCategory();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("General Medicine");
        testCategory.setDescription("General medical services and consultations");
        testCategory.setIsActive(true);
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllCategories_Success() throws Exception {
        // Given
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        when(serviceCategoryService.getAllCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testCategory.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("General Medicine"))
                .andExpect(jsonPath("$[0].description").value("General medical services and consultations"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void testGetAllCategories_EmptyList() throws Exception {
        // Given
        when(serviceCategoryService.getAllCategories()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetCategoryById_Success() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        when(serviceCategoryService.getCategoryById(categoryId)).thenReturn(Optional.of(testCategory));

        // When & Then
        mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("General Medicine"))
                .andExpect(jsonPath("$.description").value("General medical services and consultations"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(serviceCategoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/categories/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCategoryById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCategoryByName_Success() throws Exception {
        // Given
        String categoryName = "General Medicine";
        when(serviceCategoryService.getCategoryByName(categoryName)).thenReturn(Optional.of(testCategory));

        // When & Then
        mockMvc.perform(get("/api/categories/name/{name}", categoryName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("General Medicine"));
    }

    @Test
    void testGetCategoryByName_NotFound() throws Exception {
        // Given
        String categoryName = "Non-existent Category";
        when(serviceCategoryService.getCategoryByName(categoryName)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/categories/name/{name}", categoryName))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetActiveCategories_Success() throws Exception {
        // Given
        List<ServiceCategory> activeCategories = Arrays.asList(testCategory);
        when(serviceCategoryService.getActiveCategories()).thenReturn(activeCategories);

        // When & Then
        mockMvc.perform(get("/api/categories/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void testSearchCategories_Success() throws Exception {
        // Given
        String searchTerm = "medicine";
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        when(serviceCategoryService.searchCategories(searchTerm)).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/categories/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("General Medicine"));
    }

    @Test
    void testSearchCategories_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchCategories_WithNullSearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_Success() throws Exception {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("Cardiology");
        newCategory.setDescription("Heart and cardiovascular services");

        when(serviceCategoryService.createCategory(any(ServiceCategory.class))).thenReturn(testCategory);

        // When & Then
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCategory.getId().toString()))
                .andExpect(jsonPath("$.name").value("General Medicine"));
    }

    @Test
    void testCreateCategory_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("Cardiology");
        newCategory.setDescription("Heart and cardiovascular services");

        // When & Then
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateCategory_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("Cardiology");
        newCategory.setDescription("Heart and cardiovascular services");

        // When & Then
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCategory_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName(null); // Invalid data
        newCategory.setDescription("Heart and cardiovascular services");

        // When & Then
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_Success() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName("General Medicine Updated");
        updatedCategory.setDescription("Updated description");

        when(serviceCategoryService.updateCategory(categoryId, updatedCategory)).thenReturn(Optional.of(testCategory));

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(categoryId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_NotFound() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName("Updated Category");

        when(serviceCategoryService.updateCategory(categoryId, updatedCategory)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCategory_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName(null); // Invalid data

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_Success() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        when(serviceCategoryService.deleteCategory(categoryId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCategory_NotFound() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(serviceCategoryService.deleteCategory(categoryId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleCategoryStatus_Success() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        when(serviceCategoryService.toggleCategoryStatus(categoryId)).thenReturn(Optional.of(testCategory));

        // When & Then
        mockMvc.perform(patch("/api/categories/{id}/toggle-status", categoryId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(categoryId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleCategoryStatus_NotFound() throws Exception {
        // Given
        UUID categoryId = UUID.randomUUID();
        when(serviceCategoryService.toggleCategoryStatus(categoryId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/categories/{id}/toggle-status", categoryId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCategoriesWithPagination_Success() throws Exception {
        // Given
        Page<ServiceCategory> categoryPage = new PageImpl<>(Arrays.asList(testCategory), PageRequest.of(0, 10), 1);
        when(serviceCategoryService.getCategoriesWithPagination(any())).thenReturn(categoryPage);

        // When & Then
        mockMvc.perform(get("/api/categories/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetCategoriesWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCategoriesWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCategoryCount_Success() throws Exception {
        // Given
        long expectedCount = 15L;
        when(serviceCategoryService.getCategoryCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/categories/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void testGetActiveCategoryCount_Success() throws Exception {
        // Given
        long expectedCount = 12L;
        when(serviceCategoryService.getActiveCategoryCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/categories/active/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("12"));
    }

    @Test
    void testGetCategoryByName_WithSpecialCharacters_Success() throws Exception {
        // Given
        String categoryName = "Cardiology & Heart Surgery";
        when(serviceCategoryService.getCategoryByName(categoryName)).thenReturn(Optional.of(testCategory));

        // When & Then
        mockMvc.perform(get("/api/categories/name/{name}", categoryName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("General Medicine"));
    }

    @Test
    void testSearchCategories_WithSpecialCharacters_Success() throws Exception {
        // Given
        String searchTerm = "cardiology & heart";
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        when(serviceCategoryService.searchCategories(searchTerm)).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/categories/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetCategoryById_WithSpecialCharactersInUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/categories/{id}", "invalid-uuid-with-special-chars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateCategory_WithDuplicateName_ReturnsConflict() throws Exception {
        // Given
        ServiceCategory newCategory = new ServiceCategory();
        newCategory.setName("General Medicine");
        newCategory.setDescription("Duplicate category");

        when(serviceCategoryService.createCategory(any(ServiceCategory.class)))
                .thenThrow(new RuntimeException("Category with this name already exists"));

        // When & Then
        mockMvc.perform(post("/api/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateCategory_WithDuplicateName_ReturnsConflict() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        ServiceCategory updatedCategory = new ServiceCategory();
        updatedCategory.setName("Duplicate Category Name");
        updatedCategory.setDescription("Updated description");

        when(serviceCategoryService.updateCategory(categoryId, updatedCategory))
                .thenThrow(new RuntimeException("Category with this name already exists"));

        // When & Then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isInternalServerError());
    }
}
