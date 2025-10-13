package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 * 
 * Tests all business logic methods for user management including
 * registration, authentication, profile updates, and status management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

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
    void registerUser_ValidUser_ReturnsRegisteredUser() {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setPhone("+91-9876543212");
        newUser.setRole(UserRole.CUSTOMER);
        newUser.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.registerUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ExistingEmail_ThrowsException() {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("john.doe@example.com");
        newUser.setPhone("+91-9876543212");
        newUser.setRole(UserRole.CUSTOMER);

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(newUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_ValidCredentials_ReturnsUser() {
        // Given
        String email = "john.doe@example.com";
        String password = "password123";
        testUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);

        // When
        Optional<User> result = userService.authenticateUser(email, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void authenticateUser_InvalidPassword_ReturnsEmpty() {
        // Given
        String email = "john.doe@example.com";
        String password = "wrongpassword";
        testUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        // When
        Optional<User> result = userService.authenticateUser(email, password);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void authenticateUser_NonExistentEmail_ReturnsEmpty() {
        // Given
        String email = "nonexistent@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.authenticateUser(email, password);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findById_ExistingUser_ReturnsUser() {
        // Given
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getName(), result.get().getName());
    }

    @Test
    void findById_NonExistentUser_ReturnsEmpty() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(nonExistentId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_ExistingUser_ReturnsUser() {
        // Given
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_NonExistentUser_ReturnsEmpty() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail(nonExistentEmail);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void updateUser_ValidUser_ReturnsUpdatedUser() {
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

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(updatedUser);

        // Then
        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NonExistentUser_ThrowsException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        User updatedUser = new User();
        updatedUser.setId(nonExistentId);
        updatedUser.setName("Non Existent User");

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(updatedUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserStatus_ValidStatus_ReturnsUpdatedUser() {
        // Given
        UserStatus newStatus = UserStatus.INACTIVE;
        testUser.setStatus(newStatus);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUserStatus(testUser.getId(), newStatus);

        // Then
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserStatus_NonExistentUser_ThrowsException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UserStatus newStatus = UserStatus.INACTIVE;

        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> userService.updateUserStatus(nonExistentId, newStatus));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ExistingUser_ReturnsTrue() {
        // Given
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        // When
        boolean result = userService.deleteUser(testUser.getId());

        // Then
        assertTrue(result);
        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void deleteUser_NonExistentUser_ReturnsFalse() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.existsById(nonExistentId)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(nonExistentId);

        // Then
        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void findAllUsers_ReturnsAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(testUser, adminUser));

        // When
        java.util.List<User> result = userService.findAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(adminUser));
    }

    @Test
    void findByRole_ValidRole_ReturnsUsersWithRole() {
        // Given
        UserRole role = UserRole.CUSTOMER;
        when(userRepository.findByRole(role)).thenReturn(java.util.Arrays.asList(testUser));

        // When
        java.util.List<User> result = userService.findByRole(role);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0).getRole());
    }

    @Test
    void findByStatus_ValidStatus_ReturnsUsersWithStatus() {
        // Given
        UserStatus status = UserStatus.ACTIVE;
        when(userRepository.findByStatus(status)).thenReturn(java.util.Arrays.asList(testUser, adminUser));

        // When
        java.util.List<User> result = userService.findByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(user -> user.getStatus() == status));
    }
}
