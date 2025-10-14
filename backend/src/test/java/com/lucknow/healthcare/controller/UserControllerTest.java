package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.UserService;
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
 * Unit tests for UserController
 * 
 * Tests all user management endpoints including CRUD operations,
 * profile management, role updates, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("+91-9876543210");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.setIsActive(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));
    }

    @Test
    void testGetAllUsers_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetAllUsers_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById_NotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testGetUserById_OwnProfile_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        when(userService.getUserById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testGetUserById_OtherProfile_ReturnsForbidden() throws Exception {
        // Given
        UUID otherUserId = adminUser.getId();
        when(userService.getUserById(otherUserId)).thenReturn(Optional.of(adminUser));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", otherUserId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsersByRole_Success() throws Exception {
        // Given
        UserRole role = UserRole.CUSTOMER;
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByRole(role)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/role/{role}", role))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsersByStatus_Success() throws Exception {
        // Given
        UserStatus status = UserStatus.ACTIVE;
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByStatus(status)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchUsers_Success() throws Exception {
        // Given
        String searchTerm = "john";
        List<User> users = Arrays.asList(testUser);
        when(userService.searchUsers(searchTerm)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchUsers_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_Success() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setPhone("+91-9876543211");
        newUser.setRole(UserRole.CUSTOMER);

        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testCreateUser_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane.doe@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreateUser_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane.doe@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName(null); // Invalid data
        newUser.setEmail("jane.doe@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        User updatedUser = new User();
        updatedUser.setName("John Doe Updated");
        updatedUser.setPhone("+91-9876543212");

        when(userService.updateUser(userId, updatedUser)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_NotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        User updatedUser = new User();
        updatedUser.setName("Updated User");

        when(userService.updateUser(userId, updatedUser)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testUpdateUser_OwnProfile_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        User updatedUser = new User();
        updatedUser.setName("John Doe Updated");
        updatedUser.setPhone("+91-9876543212");

        when(userService.updateUser(userId, updatedUser)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testUpdateUser_OtherProfile_ReturnsForbidden() throws Exception {
        // Given
        UUID otherUserId = adminUser.getId();
        User updatedUser = new User();
        updatedUser.setName("Updated User");

        // When & Then
        mockMvc.perform(put("/api/users/{id}", otherUserId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        when(userService.deleteUser(userId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser_NotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        when(userService.deleteUser(userId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleUserStatus_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        when(userService.toggleUserStatus(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/toggle-status", userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleUserStatus_NotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        when(userService.toggleUserStatus(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/toggle-status", userId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserRole_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        UserRole newRole = UserRole.PROVIDER;

        when(userService.updateUserRole(userId, newRole)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/role", userId)
                .with(csrf())
                .param("role", newRole.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserRole_NotFound() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserRole newRole = UserRole.PROVIDER;

        when(userService.updateUserRole(userId, newRole)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/role", userId)
                .with(csrf())
                .param("role", newRole.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserRole_WithInvalidRole_ReturnsBadRequest() throws Exception {
        // Given
        UUID userId = testUser.getId();

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/role", userId)
                .with(csrf())
                .param("role", "INVALID_ROLE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsersWithPagination_Success() throws Exception {
        // Given
        Page<User> userPage = new PageImpl<>(Arrays.asList(testUser), PageRequest.of(0, 10), 1);
        when(userService.getUsersWithPagination(any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/users/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetUserCount_Success() throws Exception {
        // Given
        long expectedCount = 25L;
        when(userService.getUserCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }

    @Test
    void testGetUsersByRoleCount_Success() throws Exception {
        // Given
        UserRole role = UserRole.CUSTOMER;
        long expectedCount = 20L;
        when(userService.getUsersByRoleCount(role)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/users/role/{role}/count", role))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void testGetUsersByStatusCount_Success() throws Exception {
        // Given
        UserStatus status = UserStatus.ACTIVE;
        long expectedCount = 22L;
        when(userService.getUsersByStatusCount(status)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/users/status/{status}/count", status))
                .andExpect(status().isOk())
                .andExpect(content().string("22"));
    }

    @Test
    void testGetActiveUsers_Success() throws Exception {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userService.getActiveUsers()).thenReturn(activeUsers);

        // When & Then
        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void testGetUserById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsersByRole_WithInvalidRole_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/role/INVALID_ROLE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsersByStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsersWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsersWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}