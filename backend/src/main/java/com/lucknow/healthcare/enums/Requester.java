package com.lucknow.healthcare.enums;

/**
 * Enum for who requested the service change
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum Requester {
    /**
     * Request made by provider (requires approval)
     */
    PROVIDER,
    
    /**
     * Request made by admin (pre-approved, direct assignment)
     */
    ADMIN
}

