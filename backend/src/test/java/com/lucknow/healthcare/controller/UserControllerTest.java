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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController
 * 
 * Tests all REST endpoints for user management including
 * registration, authentication, profile management, and admin operations.
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
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@lucknowhealthcare.com");
        adminUser.setPhone("+91-9876543211");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerUser_ValidUser_ReturnsCreatedUser() throws Exception {
        // Given
        when(userService.registerUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.phone").value(testUser.getPhone()))
                .andExpect(jsonPath("$.role").value(testUser.getRole().toString()))
                .andExpect(jsonPath("$.status").value(testUser.getStatus().toString()));
    }

    @Test
    void registerUser_InvalidUser_ReturnsBadRequest() throws Exception {
        // Given
        when(userService.registerUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Invalid user data"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticateUser_ValidCredentials_ReturnsUser() throws Exception {
        // Given
        when(userService.authenticateUser(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/api/users/authenticate")
                .param("email", testUser.getEmail())
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void authenticateUser_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        when(userService.authenticateUser(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/users/authenticate")
                .param("email", "invalid@example.com")
                .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        // Given
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void getUserById_NonExistentUser_ReturnsNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userService.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_ExistingUser_ReturnsUser() throws Exception {
        // Given
        when(userService.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}", testUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void getUserByEmail_NonExistentUser_ReturnsNotFound() throws Exception {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userService.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}", nonExistentEmail))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_ValidUser_ReturnsUpdatedUser() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId(testUser.getId());
        updatedUser.setName("John Updated");
        updatedUser.setEmail(testUser.getEmail());
        updatedUser.setPhone(testUser.getPhone());
        updatedUser.setRole(testUser.getRole());
        updatedUser.setStatus(testUser.getStatus());
        updatedUser.setCreatedAt(testUser.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId().toString()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()));
    }

    @Test
    void updateUser_NonExistentUser_ReturnsNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userService.updateUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        // When & Then
        mockMvc.perform(put("/api/users/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserStatus_ValidStatus_ReturnsUpdatedUser() throws Exception {
        // Given
        testUser.setStatus(UserStatus.INACTIVE);
        when(userService.updateUserStatus(testUser.getId(), UserStatus.INACTIVE))
                .thenReturn(testUser);

        // When & Then
        mockMvc.perform(patch("/api/users/{id}/status", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.status").value(UserStatus.INACTIVE.toString()));
    }

    @Test
    void deleteUser_ExistingUser_ReturnsNoContent() throws Exception {
        // Given
        when(userService.deleteUser(testUser.getId())).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_NonExistentUser_ReturnsNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userService.deleteUser(nonExistentId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
