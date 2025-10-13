package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for User entity operations
 * 
 * Defines business logic methods for user management including
 * authentication, registration, profile management, and admin operations.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface UserService extends UserDetailsService {
    
    /**
     * Register a new user
     * 
     * @param user the user to register
     * @return the registered user
     * @throws IllegalArgumentException if email already exists
     */
    User registerUser(User user);
    
    /**
     * Authenticate user with email and password
     * 
     * @param email the user's email
     * @param password the user's password
     * @return Optional containing the user if authentication succeeds
     */
    Optional<User> authenticateUser(String email, String password);
    
    /**
     * Find user by ID
     * 
     * @param id the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(UUID id);
    
    /**
     * Find user by email
     * 
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find active user by email
     * 
     * @param email the user's email
     * @return Optional containing the active user if found
     */
    Optional<User> findActiveUserByEmail(String email);
    
    /**
     * Update user profile
     * 
     * @param user the user with updated information
     * @return the updated user
     * @throws IllegalArgumentException if user not found
     */
    User updateUser(User user);
    
    /**
     * Update user password
     * 
     * @param userId the user ID
     * @param newPassword the new password
     * @return true if password updated successfully
     * @throws IllegalArgumentException if user not found
     */
    boolean updatePassword(UUID userId, String newPassword);
    
    /**
     * Update user status
     * 
     * @param userId the user ID
     * @param status the new status
     * @return the updated user
     * @throws IllegalArgumentException if user not found
     */
    User updateUserStatus(UUID userId, UserStatus status);
    
    /**
     * Verify user email
     * 
     * @param token the email verification token
     * @return true if email verified successfully
     * @throws IllegalArgumentException if token is invalid
     */
    boolean verifyEmail(String token);
    
    /**
     * Generate password reset token
     * 
     * @param email the user's email
     * @return the password reset token
     * @throws IllegalArgumentException if user not found
     */
    String generatePasswordResetToken(String email);
    
    /**
     * Reset password with token
     * 
     * @param token the password reset token
     * @param newPassword the new password
     * @return true if password reset successfully
     * @throws IllegalArgumentException if token is invalid or expired
     */
    boolean resetPassword(String token, String newPassword);
    
    /**
     * Find users by role
     * 
     * @param role the user role
     * @return List of users with the specified role
     */
    List<User> findUsersByRole(UserRole role);
    
    /**
     * Find users by status
     * 
     * @param status the user status
     * @return List of users with the specified status
     */
    List<User> findUsersByStatus(UserStatus status);
    
    /**
     * Find active users by role
     * 
     * @param role the user role
     * @return List of active users with the specified role
     */
    List<User> findActiveUsersByRole(UserRole role);
    
    /**
     * Find users by name (case-insensitive search)
     * 
     * @param name the name to search for
     * @return List of users whose name contains the search term
     */
    List<User> findUsersByName(String name);
    
    /**
     * Get all users with pagination
     * 
     * @param pageable pagination information
     * @return Page of users
     */
    Page<User> getAllUsers(Pageable pageable);
    
    /**
     * Get users by role with pagination
     * 
     * @param role the user role
     * @param pageable pagination information
     * @return Page of users with the specified role
     */
    Page<User> getUsersByRole(UserRole role, Pageable pageable);
    
    /**
     * Get users by status with pagination
     * 
     * @param status the user status
     * @param pageable pagination information
     * @return Page of users with the specified status
     */
    Page<User> getUsersByStatus(UserStatus status, Pageable pageable);
    
    /**
     * Check if email exists
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean emailExists(String email);
    
    /**
     * Count users by role
     * 
     * @param role the user role
     * @return number of users with the specified role
     */
    long countUsersByRole(UserRole role);
    
    /**
     * Count users by status
     * 
     * @param status the user status
     * @return number of users with the specified status
     */
    long countUsersByStatus(UserStatus status);
    
    /**
     * Delete user (soft delete by setting status to INACTIVE)
     * 
     * @param userId the user ID
     * @return true if user deleted successfully
     * @throws IllegalArgumentException if user not found
     */
    boolean deleteUser(UUID userId);
    
    /**
     * Get all users
     * 
     * @return List of all users
     */
    List<User> findAllUsers();
    
    /**
     * Find users by role
     * 
     * @param role the user role
     * @return List of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find users by status
     * 
     * @param status the user status
     * @return List of users with the specified status
     */
    List<User> findByStatus(UserStatus status);
}
