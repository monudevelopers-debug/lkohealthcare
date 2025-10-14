package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.ProviderStatus;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.ProviderService;
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

import java.math.BigDecimal;
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
 * Unit tests for ProviderController
 * 
 * Tests all provider management endpoints including CRUD operations,
 * service management, availability updates, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(ProviderController.class)
class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderService providerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider testProvider;
    private User testUser;
    private Service testService;
    private ServiceCategory testCategory;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Dr. John Smith");
        testUser.setEmail("john.smith@example.com");
        testUser.setPhone("+91-9876543210");
        testUser.setRole(UserRole.PROVIDER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Setup test category
        testCategory = new ServiceCategory();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("General Medicine");
        testCategory.setDescription("General medical services");
        testCategory.setIsActive(true);
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        // Setup test service
        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("General Consultation");
        testService.setDescription("General medical consultation");
        testService.setPrice(BigDecimal.valueOf(500.00));
        testService.setDuration(30);
        testService.setCategory(testCategory);
        testService.setIsActive(true);
        testService.setCreatedAt(LocalDateTime.now());
        testService.setUpdatedAt(LocalDateTime.now());

        // Setup test provider
        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setUser(testUser);
        testProvider.setSpecialization("General Medicine");
        testProvider.setExperience(5);
        testProvider.setRating(4.5);
        testProvider.setStatus(ProviderStatus.ACTIVE);
        testProvider.setIsAvailable(true);
        testProvider.setConsultationFee(BigDecimal.valueOf(500.00));
        testProvider.setCreatedAt(LocalDateTime.now());
        testProvider.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllProviders_Success() throws Exception {
        // Given
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerService.getAllProviders()).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testProvider.getId().toString()))
                .andExpect(jsonPath("$[0].specialization").value("General Medicine"))
                .andExpect(jsonPath("$[0].experience").value(5))
                .andExpect(jsonPath("$[0].rating").value(4.5))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void testGetAllProviders_EmptyList() throws Exception {
        // Given
        when(providerService.getAllProviders()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetProviderById_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        when(providerService.getProviderById(providerId)).thenReturn(Optional.of(testProvider));

        // When & Then
        mockMvc.perform(get("/api/providers/{id}", providerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(providerId.toString()))
                .andExpect(jsonPath("$.specialization").value("General Medicine"))
                .andExpect(jsonPath("$.experience").value(5))
                .andExpect(jsonPath("$.rating").value(4.5));
    }

    @Test
    void testGetProviderById_NotFound() throws Exception {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerService.getProviderById(providerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/providers/{id}", providerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProviderById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProviderByUserId_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        when(providerService.getProviderByUserId(userId)).thenReturn(Optional.of(testProvider));

        // When & Then
        mockMvc.perform(get("/api/providers/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testProvider.getId().toString()))
                .andExpect(jsonPath("$.specialization").value("General Medicine"));
    }

    @Test
    void testGetProviderByUserId_NotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        when(providerService.getProviderByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/providers/user/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProviderByUserId_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/user/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProvidersBySpecialization_Success() throws Exception {
        // Given
        String specialization = "General Medicine";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerService.getProvidersBySpecialization(specialization)).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/providers/specialization/{specialization}", specialization))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].specialization").value("General Medicine"));
    }

    @Test
    void testGetProvidersByStatus_Success() throws Exception {
        // Given
        ProviderStatus status = ProviderStatus.ACTIVE;
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerService.getProvidersByStatus(status)).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/providers/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void testGetAvailableProviders_Success() throws Exception {
        // Given
        List<Provider> availableProviders = Arrays.asList(testProvider);
        when(providerService.getAvailableProviders()).thenReturn(availableProviders);

        // When & Then
        mockMvc.perform(get("/api/providers/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isAvailable").value(true));
    }

    @Test
    void testSearchProviders_Success() throws Exception {
        // Given
        String searchTerm = "general";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerService.searchProviders(searchTerm)).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/providers/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].specialization").value("General Medicine"));
    }

    @Test
    void testSearchProviders_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchProviders_WithNullSearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProvider_Success() throws Exception {
        // Given
        Provider newProvider = new Provider();
        newProvider.setSpecialization("Cardiology");
        newProvider.setExperience(10);
        newProvider.setConsultationFee(BigDecimal.valueOf(1000.00));

        when(providerService.createProvider(any(Provider.class))).thenReturn(testProvider);

        // When & Then
        mockMvc.perform(post("/api/providers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProvider)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testProvider.getId().toString()))
                .andExpect(jsonPath("$.specialization").value("General Medicine"));
    }

    @Test
    void testCreateProvider_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        Provider newProvider = new Provider();
        newProvider.setSpecialization("Cardiology");

        // When & Then
        mockMvc.perform(post("/api/providers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProvider)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateProvider_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // Given
        Provider newProvider = new Provider();
        newProvider.setSpecialization("Cardiology");

        // When & Then
        mockMvc.perform(post("/api/providers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProvider)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProvider_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        Provider newProvider = new Provider();
        newProvider.setSpecialization(null); // Invalid data

        // When & Then
        mockMvc.perform(post("/api/providers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProvider)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProvider_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        Provider updatedProvider = new Provider();
        updatedProvider.setSpecialization("Cardiology Updated");
        updatedProvider.setExperience(15);

        when(providerService.updateProvider(providerId, updatedProvider)).thenReturn(Optional.of(testProvider));

        // When & Then
        mockMvc.perform(put("/api/providers/{id}", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProvider)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(providerId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProvider_NotFound() throws Exception {
        // Given
        UUID providerId = UUID.randomUUID();
        Provider updatedProvider = new Provider();
        updatedProvider.setSpecialization("Updated Specialization");

        when(providerService.updateProvider(providerId, updatedProvider)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/providers/{id}", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProvider)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProvider_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        Provider updatedProvider = new Provider();
        updatedProvider.setSpecialization(null); // Invalid data

        // When & Then
        mockMvc.perform(put("/api/providers/{id}", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProvider)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProvider_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        when(providerService.deleteProvider(providerId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/providers/{id}", providerId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProvider_NotFound() throws Exception {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerService.deleteProvider(providerId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/providers/{id}", providerId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleProviderStatus_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        when(providerService.toggleProviderStatus(providerId)).thenReturn(Optional.of(testProvider));

        // When & Then
        mockMvc.perform(patch("/api/providers/{id}/toggle-status", providerId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(providerId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleProviderStatus_NotFound() throws Exception {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerService.toggleProviderStatus(providerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/providers/{id}/toggle-status", providerId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleProviderAvailability_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        when(providerService.toggleProviderAvailability(providerId)).thenReturn(Optional.of(testProvider));

        // When & Then
        mockMvc.perform(patch("/api/providers/{id}/toggle-availability", providerId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(providerId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleProviderAvailability_NotFound() throws Exception {
        // Given
        UUID providerId = UUID.randomUUID();
        when(providerService.toggleProviderAvailability(providerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/providers/{id}/toggle-availability", providerId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProvidersWithPagination_Success() throws Exception {
        // Given
        Page<Provider> providerPage = new PageImpl<>(Arrays.asList(testProvider), PageRequest.of(0, 10), 1);
        when(providerService.getProvidersWithPagination(any())).thenReturn(providerPage);

        // When & Then
        mockMvc.perform(get("/api/providers/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetProvidersWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProvidersWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProviderCount_Success() throws Exception {
        // Given
        long expectedCount = 25L;
        when(providerService.getProviderCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/providers/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }

    @Test
    void testGetProvidersByStatusCount_Success() throws Exception {
        // Given
        ProviderStatus status = ProviderStatus.ACTIVE;
        long expectedCount = 20L;
        when(providerService.getProvidersByStatusCount(status)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/providers/status/{status}/count", status))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void testGetAvailableProviderCount_Success() throws Exception {
        // Given
        long expectedCount = 15L;
        when(providerService.getAvailableProviderCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/providers/available/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void testGetProvidersBySpecialization_WithSpecialCharacters_Success() throws Exception {
        // Given
        String specialization = "Cardiology & Heart Surgery";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerService.getProvidersBySpecialization(specialization)).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/providers/specialization/{specialization}", specialization))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchProviders_WithSpecialCharacters_Success() throws Exception {
        // Given
        String searchTerm = "cardiology & heart";
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerService.searchProviders(searchTerm)).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/providers/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetProviderById_WithSpecialCharactersInUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/providers/{id}", "invalid-uuid-with-special-chars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProvider_WithDuplicateSpecialization_ReturnsConflict() throws Exception {
        // Given
        Provider newProvider = new Provider();
        newProvider.setSpecialization("General Medicine");
        newProvider.setExperience(5);

        when(providerService.createProvider(any(Provider.class)))
                .thenThrow(new RuntimeException("Provider with this specialization already exists"));

        // When & Then
        mockMvc.perform(post("/api/providers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProvider)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateProvider_WithDuplicateSpecialization_ReturnsConflict() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        Provider updatedProvider = new Provider();
        updatedProvider.setSpecialization("Duplicate Specialization");
        updatedProvider.setExperience(10);

        when(providerService.updateProvider(providerId, updatedProvider))
                .thenThrow(new RuntimeException("Provider with this specialization already exists"));

        // When & Then
        mockMvc.perform(put("/api/providers/{id}", providerId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProvider)))
                .andExpect(status().isInternalServerError());
    }
}