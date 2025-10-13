package com.lucknow.healthcare.enums;

/**
 * User status enumeration for account management
 * 
 * Defines the different states a user account can be in:
 * - ACTIVE: User can access the system normally
 * - INACTIVE: User account is temporarily disabled
 * - SUSPENDED: User account is suspended due to policy violations
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}
