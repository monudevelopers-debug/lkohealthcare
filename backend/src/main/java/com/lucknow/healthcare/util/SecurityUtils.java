package com.lucknow.healthcare.util;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for security-related operations
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Component
public class SecurityUtils {
    
    private static UserRepository userRepository;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        SecurityUtils.userRepository = userRepository;
    }
    
    /**
     * Get the current authenticated user's ID
     * 
     * @return UUID of current user
     * @throws SecurityException if user is not authenticated
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new SecurityException("User not found: " + email));
        
        return user.getId();
    }
    
    /**
     * Get the current authenticated user's email
     * 
     * @return email of current user
     * @throws SecurityException if user is not authenticated
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        
        return authentication.getName();
    }
    
    /**
     * Get the current authenticated user
     * 
     * @return User entity
     * @throws SecurityException if user is not authenticated
     */
    public static User getCurrentUser() {
        String email = getCurrentUserEmail();
        
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new SecurityException("User not found: " + email));
    }
    
    /**
     * Check if current user has a specific role
     * 
     * @param role the role to check
     * @return true if user has the role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}

