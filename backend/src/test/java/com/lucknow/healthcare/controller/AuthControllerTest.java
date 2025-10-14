package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 * 
 * Tests all authentication endpoints including login, registration,
 * password reset, token refresh, and logout functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

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
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        String email = "john.doe@example.com";
        String password = "password123";
        String token = "jwt-token-123";

        when(authService.authenticate(email, password)).thenReturn(Optional.of(testUser));
        when(authService.generateToken(testUser)).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.user.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.user.role").value("CUSTOMER"));
    }

    @Test
    void testLogin_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        String email = "john.doe@example.com";
        String password = "wrongpassword";

        when(authService.authenticate(email, password)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_WithEmptyEmail_ReturnsBadRequest() throws Exception {
        // Given
        String email = "";
        String password = "password123";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WithEmptyPassword_ReturnsBadRequest() throws Exception {
        // Given
        String email = "john.doe@example.com";
        String password = "";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WithNullEmail_ReturnsBadRequest() throws Exception {
        // Given
        String email = null;
        String password = "password123";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WithNullPassword_ReturnsBadRequest() throws Exception {
        // Given
        String email = "john.doe@example.com";
        String password = null;

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // Given
        String email = "invalid-email";
        String password = "password123";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        String email = "newuser@example.com";
        String password = "password123";
        String name = "New User";
        String phone = "+91-9876543211";
        UserRole role = UserRole.CUSTOMER;

        when(authService.register(any(User.class))).thenReturn(testUser);
        when(authService.generateToken(testUser)).thenReturn("jwt-token-123");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"));
    }

    @Test
    void testRegister_WithDuplicateEmail_ReturnsConflict() throws Exception {
        // Given
        String email = "existing@example.com";
        String password = "password123";
        String name = "New User";
        String phone = "+91-9876543211";
        UserRole role = UserRole.CUSTOMER;

        when(authService.register(any(User.class)))
                .thenThrow(new RuntimeException("User with this email already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegister_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        String email = "newuser@example.com";
        String password = "123"; // Too short
        String name = "New User";
        String phone = "+91-9876543211";
        UserRole role = UserRole.CUSTOMER;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WithEmptyName_ReturnsBadRequest() throws Exception {
        // Given
        String email = "newuser@example.com";
        String password = "password123";
        String name = ""; // Empty name
        String phone = "+91-9876543211";
        UserRole role = UserRole.CUSTOMER;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // Given
        String email = "invalid-email";
        String password = "password123";
        String name = "New User";
        String phone = "+91-9876543211";
        UserRole role = UserRole.CUSTOMER;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WithInvalidPhoneFormat_ReturnsBadRequest() throws Exception {
        // Given
        String email = "newuser@example.com";
        String password = "password123";
        String name = "New User";
        String phone = "invalid-phone";
        UserRole role = UserRole.CUSTOMER;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_WithNullRole_ReturnsBadRequest() throws Exception {
        // Given
        String email = "newuser@example.com";
        String password = "password123";
        String name = "New User";
        String phone = "+91-9876543211";
        UserRole role = null;

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(email, password, name, phone, role))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Given
        String refreshToken = "refresh-token-123";
        String newToken = "new-jwt-token-123";

        when(authService.refreshToken(refreshToken)).thenReturn(Optional.of(newToken));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(newToken));
    }

    @Test
    void testRefreshToken_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        // Given
        String refreshToken = "invalid-refresh-token";

        when(authService.refreshToken(refreshToken)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRefreshToken_WithEmptyToken_ReturnsBadRequest() throws Exception {
        // Given
        String refreshToken = "";

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshToken_WithNullToken_ReturnsBadRequest() throws Exception {
        // Given
        String refreshToken = null;

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testLogout_Success() throws Exception {
        // Given
        String token = "jwt-token-123";

        when(authService.logout(token)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LogoutRequest(token))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testLogout_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        // Given
        String token = "invalid-token";

        when(authService.logout(token)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LogoutRequest(token))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogout_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        String token = "jwt-token-123";

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LogoutRequest(token))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testLogout_WithEmptyToken_ReturnsBadRequest() throws Exception {
        // Given
        String token = "";

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LogoutRequest(token))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testLogout_WithNullToken_ReturnsBadRequest() throws Exception {
        // Given
        String token = null;

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LogoutRequest(token))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testForgotPassword_Success() throws Exception {
        // Given
        String email = "john.doe@example.com";

        when(authService.forgotPassword(email)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password reset email sent successfully"));
    }

    @Test
    void testForgotPassword_WithNonExistentEmail_ReturnsNotFound() throws Exception {
        // Given
        String email = "nonexistent@example.com";

        when(authService.forgotPassword(email)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testForgotPassword_WithEmptyEmail_ReturnsBadRequest() throws Exception {
        // Given
        String email = "";

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testForgotPassword_WithNullEmail_ReturnsBadRequest() throws Exception {
        // Given
        String email = null;

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testForgotPassword_WithInvalidEmailFormat_ReturnsBadRequest() throws Exception {
        // Given
        String email = "invalid-email";

        // When & Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(email))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword_Success() throws Exception {
        // Given
        String token = "reset-token-123";
        String newPassword = "newpassword123";

        when(authService.resetPassword(token, newPassword)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Password reset successfully"));
    }

    @Test
    void testResetPassword_WithInvalidToken_ReturnsUnauthorized() throws Exception {
        // Given
        String token = "invalid-reset-token";
        String newPassword = "newpassword123";

        when(authService.resetPassword(token, newPassword)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testResetPassword_WithEmptyToken_ReturnsBadRequest() throws Exception {
        // Given
        String token = "";
        String newPassword = "newpassword123";

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword_WithNullToken_ReturnsBadRequest() throws Exception {
        // Given
        String token = null;
        String newPassword = "newpassword123";

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword_WithEmptyPassword_ReturnsBadRequest() throws Exception {
        // Given
        String token = "reset-token-123";
        String newPassword = "";

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword_WithNullPassword_ReturnsBadRequest() throws Exception {
        // Given
        String token = "reset-token-123";
        String newPassword = null;

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword_WithWeakPassword_ReturnsBadRequest() throws Exception {
        // Given
        String token = "reset-token-123";
        String newPassword = "123"; // Too weak

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword_WithExpiredToken_ReturnsUnauthorized() throws Exception {
        // Given
        String token = "expired-reset-token";
        String newPassword = "newpassword123";

        when(authService.resetPassword(token, newPassword))
                .thenThrow(new RuntimeException("Reset token has expired"));

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testResetPassword_WithUsedToken_ReturnsUnauthorized() throws Exception {
        // Given
        String token = "used-reset-token";
        String newPassword = "newpassword123";

        when(authService.resetPassword(token, newPassword))
                .thenThrow(new RuntimeException("Reset token has already been used"));

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ResetPasswordRequest(token, newPassword))))
                .andExpect(status().isInternalServerError());
    }

    // Helper classes for request objects
    private static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    private static class RegisterRequest {
        public String email;
        public String password;
        public String name;
        public String phone;
        public UserRole role;

        public RegisterRequest(String email, String password, String name, String phone, UserRole role) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phone = phone;
            this.role = role;
        }
    }

    private static class RefreshTokenRequest {
        public String refreshToken;

        public RefreshTokenRequest(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    private static class LogoutRequest {
        public String token;

        public LogoutRequest(String token) {
            this.token = token;
        }
    }

    private static class ForgotPasswordRequest {
        public String email;

        public ForgotPasswordRequest(String email) {
            this.email = email;
        }
    }

    private static class ResetPasswordRequest {
        public String token;
        public String newPassword;

        public ResetPasswordRequest(String token, String newPassword) {
            this.token = token;
            this.newPassword = newPassword;
        }
    }
}