package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity
 * 
 * Provides data access methods for user management including
 * authentication, role-based queries, and status filtering.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find user by email address
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by email and status
     * 
     * @param email the email address to search for
     * @param status the user status to filter by
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    
    /**
     * Find users by role
     * 
     * @param role the user role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find users by status
     * 
     * @param status the user status to filter by
     * @return List of users with the specified status
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * Find users by role and status
     * 
     * @param role the user role to filter by
     * @param status the user status to filter by
     * @return List of users with the specified role and status
     */
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    
    /**
     * Find active users by role
     * 
     * @param role the user role to filter by
     * @return List of active users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
    
    /**
     * Check if email exists
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by email verification status
     * 
     * @param emailVerified the email verification status
     * @return List of users with the specified email verification status
     */
    List<User> findByEmailVerified(Boolean emailVerified);
    
    /**
     * Find users by name containing (case-insensitive)
     * 
     * @param name the name to search for
     * @return List of users whose name contains the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Count users by role
     * 
     * @param role the user role to count
     * @return number of users with the specified role
     */
    long countByRole(UserRole role);
    
    /**
     * Count users by status
     * 
     * @param status the user status to count
     * @return number of users with the specified status
     */
    long countByStatus(UserStatus status);
    
    // Pageable methods
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    // Additional query methods
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);
}
