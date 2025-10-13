package com.lucknow.healthcare.integration;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Database Connection Test
 * 
 * Tests database connectivity and basic CRUD operations.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DatabaseConnectionTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void testDatabaseConnection() {
        // Test basic database connectivity
        assertNotNull(userRepository);
    }
    
    @Test
    public void testUserCrudOperations() {
        // Create a test user
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);
        user.setPhone("1234567890");
        
        // Save user
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        assertEquals("Test User", savedUser.getName());
        assertEquals("test@example.com", savedUser.getEmail());
        
        // Find user by email
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getName());
        
        // Update user
        savedUser.setName("Updated Test User");
        User updatedUser = userRepository.save(savedUser);
        assertEquals("Updated Test User", updatedUser.getName());
        
        // Delete user
        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findByEmail("test@example.com");
        assertFalse(deletedUser.isPresent());
    }
    
    @Test
    public void testUserRepositoryQueries() {
        // Create test users
        User user1 = new User("User 1", "user1@example.com", "password", UserRole.CUSTOMER);
        User user2 = new User("User 2", "user2@example.com", "password", UserRole.PROVIDER);
        User user3 = new User("User 3", "user3@example.com", "password", UserRole.ADMIN);
        
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        
        // Test role-based queries
        assertEquals(1, userRepository.countByRole(UserRole.CUSTOMER));
        assertEquals(1, userRepository.countByRole(UserRole.PROVIDER));
        assertEquals(1, userRepository.countByRole(UserRole.ADMIN));
        
        // Test status queries
        assertEquals(3, userRepository.countByStatus(UserStatus.ACTIVE));
        
        // Test email existence
        assertTrue(userRepository.existsByEmail("user1@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
        
        // Clean up
        userRepository.deleteAll();
    }
}
