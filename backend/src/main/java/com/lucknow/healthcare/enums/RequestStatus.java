package com.lucknow.healthcare.enums;

/**
 * Enum for service request status
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum RequestStatus {
    /**
     * Request is awaiting admin review
     */
    PENDING,
    
    /**
     * Request has been approved by admin
     */
    APPROVED,
    
    /**
     * Request has been rejected by admin
     */
    REJECTED
}

