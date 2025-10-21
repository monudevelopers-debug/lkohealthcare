package com.lucknow.healthcare.service.interfaces;

import com.lucknow.healthcare.entity.ServiceRequest;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.RequestType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for ServiceRequest entity operations
 * 
 * Defines business logic methods for service request management including
 * provider requests, admin approval/rejection, and workflow management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public interface ServiceRequestService {
    
    /**
     * Provider requests to add a service
     * 
     * @param providerId the provider ID
     * @param serviceId the service ID
     * @param notes optional notes from provider
     * @return the created service request
     * @throws IllegalArgumentException if provider or service not found
     * @throws IllegalStateException if duplicate pending request exists
     */
    ServiceRequest requestServiceAddition(UUID providerId, UUID serviceId, String notes);
    
    /**
     * Provider requests to remove a service
     * 
     * @param providerId the provider ID
     * @param serviceId the service ID
     * @param notes optional notes from provider
     * @return the created service request
     * @throws IllegalArgumentException if provider or service not found
     * @throws IllegalStateException if duplicate pending request exists
     */
    ServiceRequest requestServiceRemoval(UUID providerId, UUID serviceId, String notes);
    
    /**
     * Admin approves a service request
     * 
     * @param requestId the request ID
     * @param adminId the admin user ID
     * @return the approved request
     * @throws IllegalArgumentException if request or admin not found
     * @throws IllegalStateException if request is not pending
     */
    ServiceRequest approveRequest(UUID requestId, UUID adminId);
    
    /**
     * Admin rejects a service request
     * 
     * @param requestId the request ID
     * @param adminId the admin user ID
     * @param reason the rejection reason
     * @return the rejected request
     * @throws IllegalArgumentException if request or admin not found
     * @throws IllegalStateException if request is not pending
     */
    ServiceRequest rejectRequest(UUID requestId, UUID adminId, String reason);
    
    /**
     * Get all pending requests (for admin review)
     * 
     * @return List of pending service requests
     */
    List<ServiceRequest> getAllPendingRequests();
    
    /**
     * Get pending requests for a specific provider
     * 
     * @param providerId the provider ID
     * @return List of pending requests for the provider
     */
    List<ServiceRequest> getPendingRequestsByProvider(UUID providerId);
    
    /**
     * Get all requests for a specific provider
     * 
     * @param providerId the provider ID
     * @return List of all requests for the provider
     */
    List<ServiceRequest> getAllRequestsByProvider(UUID providerId);
    
    /**
     * Find service request by ID
     * 
     * @param requestId the request ID
     * @return Optional containing the request if found
     */
    Optional<ServiceRequest> findById(UUID requestId);
    
    /**
     * Count pending requests
     * 
     * @return number of pending requests
     */
    long countPendingRequests();
    
    /**
     * Count pending requests for a provider
     * 
     * @param providerId the provider ID
     * @return number of pending requests for the provider
     */
    long countPendingRequestsByProvider(UUID providerId);
}

