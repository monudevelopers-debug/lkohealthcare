package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for User entity operations
 * 
 * Provides REST endpoints for user management including
 * authentication, registration, profile management, and admin operations.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Register a new user
     * 
     * @param user the user to register
     * @return ResponseEntity containing the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Authenticate user
     * 
     * @param email the user's email
     * @param password the user's password
     * @return ResponseEntity containing the authenticated user
     */
    @PostMapping("/authenticate")
    public ResponseEntity<User> authenticateUser(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOpt = userService.authenticateUser(email, password);
        return userOpt.map(user -> ResponseEntity.ok(user))
                     .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
    
    /**
     * Get all users with pagination
     * 
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @return ResponseEntity containing paginated users
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVIDER', 'CUSTOMER')")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            logger.info("GET /users called with page={}, size={}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userService.getAllUsers(pageable);
            logger.info("Returning {} users", users.getContent().size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error in getUsers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get user by ID
     * 
     * @param id the user ID
     * @return ResponseEntity containing the user if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        Optional<User> userOpt = userService.findById(id);
        return userOpt.map(user -> ResponseEntity.ok(user))
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get user by email
     * 
     * @param email the user's email
     * @return ResponseEntity containing the user if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        return userOpt.map(user -> ResponseEntity.ok(user))
                     .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update user profile
     * 
     * @param id the user ID
     * @param user the updated user information
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update user password
     * 
     * @param id the user ID
     * @param newPassword the new password
     * @return ResponseEntity indicating success or failure
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @RequestParam String newPassword) {
        try {
            boolean success = userService.updatePassword(id, newPassword);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update user status
     * 
     * @param id the user ID
     * @param status the new status
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserStatus(@PathVariable UUID id, @RequestParam UserStatus status) {
        try {
            User updatedUser = userService.updateUserStatus(id, status);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update user role
     * 
     * @param id the user ID
     * @param role the new role
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserRole(@PathVariable UUID id, @RequestParam UserRole role) {
        try {
            User updatedUser = userService.updateUserRole(id, role);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Verify user email
     * 
     * @param token the email verification token
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        try {
            boolean success = userService.verifyEmail(token);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Generate password reset token
     * 
     * @param email the user's email
     * @return ResponseEntity containing the reset token
     */
    @PostMapping("/reset-password-token")
    public ResponseEntity<String> generatePasswordResetToken(@RequestParam String email) {
        try {
            String token = userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Reset password with token
     * 
     * @param token the password reset token
     * @param newPassword the new password
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            boolean success = userService.resetPassword(token, newPassword);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get users by role
     * 
     * @param role the user role
     * @return ResponseEntity containing the list of users
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.findUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get users by status
     * 
     * @param status the user status
     * @return ResponseEntity containing the list of users
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = userService.findUsersByStatus(status);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get active users by role
     * 
     * @param role the user role
     * @return ResponseEntity containing the list of active users
     */
    @GetMapping("/active/role/{role}")
    public ResponseEntity<List<User>> getActiveUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.findActiveUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Search users by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of users
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.findUsersByName(name);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get users by role with pagination
     * 
     * @param role the user role
     * @param pageable pagination information
     * @return ResponseEntity containing the page of users
     */
    @GetMapping("/role/{role}/page")
    public ResponseEntity<Page<User>> getUsersByRole(@PathVariable UserRole role, Pageable pageable) {
        Page<User> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get users by status with pagination
     * 
     * @param status the user status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of users
     */
    @GetMapping("/status/{status}/page")
    public ResponseEntity<Page<User>> getUsersByStatus(@PathVariable UserStatus status, Pageable pageable) {
        Page<User> users = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Check if email exists
     * 
     * @param email the email to check
     * @return ResponseEntity containing the existence status
     */
    @GetMapping("/email-exists/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Count users by role
     * 
     * @param role the user role
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Long> countUsersByRole(@PathVariable UserRole role) {
        long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count users by status
     * 
     * @param status the user status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countUsersByStatus(@PathVariable UserStatus status) {
        long count = userService.countUsersByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Delete user
     * 
     * @param id the user ID
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            boolean success = userService.deleteUser(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
