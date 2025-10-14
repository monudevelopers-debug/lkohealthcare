package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.service.interfaces.ServiceService;
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
 * Unit tests for ServiceController
 * 
 * Tests all service management endpoints including CRUD operations,
 * search functionality, category filtering, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(ServiceController.class)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Service testService;
    private ServiceCategory testCategory;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testGetAllServices_Success() throws Exception {
        // Given
        List<Service> services = Arrays.asList(testService);
        when(serviceService.getAllServices()).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testService.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("General Consultation"))
                .andExpect(jsonPath("$[0].description").value("General medical consultation"))
                .andExpect(jsonPath("$[0].price").value(500.00))
                .andExpect(jsonPath("$[0].duration").value(30))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void testGetAllServices_EmptyList() throws Exception {
        // Given
        when(serviceService.getAllServices()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetServiceById_Success() throws Exception {
        // Given
        UUID serviceId = testService.getId();
        when(serviceService.getServiceById(serviceId)).thenReturn(Optional.of(testService));

        // When & Then
        mockMvc.perform(get("/api/services/{id}", serviceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(serviceId.toString()))
                .andExpect(jsonPath("$.name").value("General Consultation"))
                .andExpect(jsonPath("$.description").value("General medical consultation"))
                .andExpect(jsonPath("$.price").value(500.00))
                .andExpect(jsonPath("$.duration").value(30));
    }

    @Test
    void testGetServiceById_NotFound() throws Exception {
        // Given
        UUID serviceId = UUID.randomUUID();
        when(serviceService.getServiceById(serviceId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/services/{id}", serviceId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetServiceById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByCategory_Success() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        List<Service> services = Arrays.asList(testService);
        when(serviceService.getServicesByCategory(categoryId)).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services/category/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testService.getId().toString()));
    }

    @Test
    void testGetServicesByCategory_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/category/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetActiveServices_Success() throws Exception {
        // Given
        List<Service> activeServices = Arrays.asList(testService);
        when(serviceService.getActiveServices()).thenReturn(activeServices);

        // When & Then
        mockMvc.perform(get("/api/services/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void testGetServicesByPriceRange_Success() throws Exception {
        // Given
        BigDecimal minPrice = BigDecimal.valueOf(400.00);
        BigDecimal maxPrice = BigDecimal.valueOf(600.00);
        List<Service> services = Arrays.asList(testService);
        when(serviceService.getServicesByPriceRange(minPrice, maxPrice)).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services/price-range")
                .param("minPrice", "400.00")
                .param("maxPrice", "600.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].price").value(500.00));
    }

    @Test
    void testGetServicesByPriceRange_WithInvalidMinPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range")
                .param("minPrice", "-100.00")
                .param("maxPrice", "600.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRange_WithInvalidMaxPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range")
                .param("minPrice", "400.00")
                .param("maxPrice", "0.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRange_WithMinPriceGreaterThanMaxPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range")
                .param("minPrice", "600.00")
                .param("maxPrice", "400.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRange_WithMissingMaxPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range")
                .param("minPrice", "400.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRange_WithMissingMinPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range")
                .param("maxPrice", "600.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchServices_Success() throws Exception {
        // Given
        String searchTerm = "consultation";
        List<Service> services = Arrays.asList(testService);
        when(serviceService.searchServices(searchTerm)).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/services/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("General Consultation"));
    }

    @Test
    void testSearchServices_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchServices_WithNullSearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateService_Success() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName("Cardiology Consultation");
        newService.setDescription("Heart and cardiovascular consultation");
        newService.setPrice(BigDecimal.valueOf(1000.00));
        newService.setDuration(45);

        when(serviceService.createService(any(Service.class))).thenReturn(testService);

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testService.getId().toString()))
                .andExpect(jsonPath("$.name").value("General Consultation"));
    }

    @Test
    void testCreateService_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName("Cardiology Consultation");
        newService.setDescription("Heart and cardiovascular consultation");

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateService_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName("Cardiology Consultation");
        newService.setDescription("Heart and cardiovascular consultation");

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateService_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName(null); // Invalid data
        newService.setDescription("Heart and cardiovascular consultation");

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateService_WithNegativePrice_ReturnsBadRequest() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName("Cardiology Consultation");
        newService.setDescription("Heart and cardiovascular consultation");
        newService.setPrice(BigDecimal.valueOf(-100.00)); // Invalid price

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateService_WithNegativeDuration_ReturnsBadRequest() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName("Cardiology Consultation");
        newService.setDescription("Heart and cardiovascular consultation");
        newService.setDuration(-30); // Invalid duration

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateService_Success() throws Exception {
        // Given
        UUID serviceId = testService.getId();
        Service updatedService = new Service();
        updatedService.setName("General Consultation Updated");
        updatedService.setDescription("Updated description");
        updatedService.setPrice(BigDecimal.valueOf(600.00));

        when(serviceService.updateService(serviceId, updatedService)).thenReturn(Optional.of(testService));

        // When & Then
        mockMvc.perform(put("/api/services/{id}", serviceId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedService)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(serviceId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateService_NotFound() throws Exception {
        // Given
        UUID serviceId = UUID.randomUUID();
        Service updatedService = new Service();
        updatedService.setName("Updated Service");

        when(serviceService.updateService(serviceId, updatedService)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/services/{id}", serviceId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedService)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateService_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UUID serviceId = testService.getId();
        Service updatedService = new Service();
        updatedService.setName(null); // Invalid data

        // When & Then
        mockMvc.perform(put("/api/services/{id}", serviceId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedService)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteService_Success() throws Exception {
        // Given
        UUID serviceId = testService.getId();
        when(serviceService.deleteService(serviceId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/services/{id}", serviceId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteService_NotFound() throws Exception {
        // Given
        UUID serviceId = UUID.randomUUID();
        when(serviceService.deleteService(serviceId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/services/{id}", serviceId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleServiceStatus_Success() throws Exception {
        // Given
        UUID serviceId = testService.getId();
        when(serviceService.toggleServiceStatus(serviceId)).thenReturn(Optional.of(testService));

        // When & Then
        mockMvc.perform(patch("/api/services/{id}/toggle-status", serviceId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(serviceId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleServiceStatus_NotFound() throws Exception {
        // Given
        UUID serviceId = UUID.randomUUID();
        when(serviceService.toggleServiceStatus(serviceId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/services/{id}/toggle-status", serviceId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetServicesWithPagination_Success() throws Exception {
        // Given
        Page<Service> servicePage = new PageImpl<>(Arrays.asList(testService), PageRequest.of(0, 10), 1);
        when(serviceService.getServicesWithPagination(any())).thenReturn(servicePage);

        // When & Then
        mockMvc.perform(get("/api/services/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetServicesWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServiceCount_Success() throws Exception {
        // Given
        long expectedCount = 25L;
        when(serviceService.getServiceCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/services/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }

    @Test
    void testGetActiveServiceCount_Success() throws Exception {
        // Given
        long expectedCount = 20L;
        when(serviceService.getActiveServiceCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/services/active/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void testGetServicesByCategoryCount_Success() throws Exception {
        // Given
        UUID categoryId = testCategory.getId();
        long expectedCount = 15L;
        when(serviceService.getServicesByCategoryCount(categoryId)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/services/category/{categoryId}/count", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void testGetServicesByCategoryCount_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/category/invalid-uuid/count"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRangeCount_Success() throws Exception {
        // Given
        BigDecimal minPrice = BigDecimal.valueOf(400.00);
        BigDecimal maxPrice = BigDecimal.valueOf(600.00);
        long expectedCount = 10L;
        when(serviceService.getServicesByPriceRangeCount(minPrice, maxPrice)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/services/price-range/count")
                .param("minPrice", "400.00")
                .param("maxPrice", "600.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void testGetServicesByPriceRangeCount_WithInvalidMinPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range/count")
                .param("minPrice", "-100.00")
                .param("maxPrice", "600.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRangeCount_WithInvalidMaxPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range/count")
                .param("minPrice", "400.00")
                .param("maxPrice", "0.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRangeCount_WithMinPriceGreaterThanMaxPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range/count")
                .param("minPrice", "600.00")
                .param("maxPrice", "400.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRangeCount_WithMissingMaxPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range/count")
                .param("minPrice", "400.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServicesByPriceRangeCount_WithMissingMinPrice_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/price-range/count")
                .param("maxPrice", "600.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetServiceById_WithSpecialCharactersInUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/services/{id}", "invalid-uuid-with-special-chars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateService_WithDuplicateName_ReturnsConflict() throws Exception {
        // Given
        Service newService = new Service();
        newService.setName("General Consultation");
        newService.setDescription("Duplicate service");

        when(serviceService.createService(any(Service.class)))
                .thenThrow(new RuntimeException("Service with this name already exists"));

        // When & Then
        mockMvc.perform(post("/api/services")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newService)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateService_WithDuplicateName_ReturnsConflict() throws Exception {
        // Given
        UUID serviceId = testService.getId();
        Service updatedService = new Service();
        updatedService.setName("Duplicate Service Name");
        updatedService.setDescription("Updated description");

        when(serviceService.updateService(serviceId, updatedService))
                .thenThrow(new RuntimeException("Service with this name already exists"));

        // When & Then
        mockMvc.perform(put("/api/services/{id}", serviceId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedService)))
                .andExpect(status().isInternalServerError());
    }
}