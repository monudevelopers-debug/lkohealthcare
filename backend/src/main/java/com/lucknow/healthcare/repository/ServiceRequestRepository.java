package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.ServiceRequest;
import com.lucknow.healthcare.enums.RequestStatus;
import com.lucknow.healthcare.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ServiceRequest entity
 * 
 * Provides data access methods for service request management including
 * approval workflow, provider requests, and admin review.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {
    
    /**
     * Find all requests by provider
     * 
     * @param provider the provider
     * @return List of service requests for the provider
     */
    List<ServiceRequest> findByProvider(Provider provider);
    
    /**
     * Find requests by provider ID
     * 
     * @param providerId the provider ID
     * @return List of service requests for the provider
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.provider.id = :providerId ORDER BY sr.requestedAt DESC")
    List<ServiceRequest> findByProviderId(@Param("providerId") UUID providerId);
    
    /**
     * Find all pending requests
     * 
     * @return List of pending service requests
     */
    List<ServiceRequest> findByStatusOrderByRequestedAtDesc(RequestStatus status);
    
    /**
     * Find pending requests for a provider
     * 
     * @param providerId the provider ID
     * @param status the request status
     * @return List of pending service requests for the provider
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.provider.id = :providerId AND sr.status = :status ORDER BY sr.requestedAt DESC")
    List<ServiceRequest> findByProviderIdAndStatus(@Param("providerId") UUID providerId, @Param("status") RequestStatus status);
    
    /**
     * Find pending request for a specific provider-service combination
     * 
     * @param providerId the provider ID
     * @param serviceId the service ID
     * @param requestType the request type
     * @param status the request status
     * @return Optional containing the request if found
     */
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.provider.id = :providerId AND sr.service.id = :serviceId AND sr.requestType = :requestType AND sr.status = :status")
    Optional<ServiceRequest> findPendingRequest(@Param("providerId") UUID providerId, 
                                                 @Param("serviceId") UUID serviceId, 
                                                 @Param("requestType") RequestType requestType,
                                                 @Param("status") RequestStatus status);
    
    /**
     * Count pending requests
     * 
     * @return number of pending requests
     */
    long countByStatus(RequestStatus status);
    
    /**
     * Count pending requests for a provider
     * 
     * @param providerId the provider ID
     * @param status the request status
     * @return number of pending requests for the provider
     */
    @Query("SELECT COUNT(sr) FROM ServiceRequest sr WHERE sr.provider.id = :providerId AND sr.status = :status")
    long countByProviderIdAndStatus(@Param("providerId") UUID providerId, @Param("status") RequestStatus status);
    
    /**
     * Find all pending requests (shortcut)
     * 
     * @return List of pending service requests
     */
    default List<ServiceRequest> findAllPending() {
        return findByStatusOrderByRequestedAtDesc(RequestStatus.PENDING);
    }
    
    /**
     * Find pending requests for a provider (shortcut)
     * 
     * @param providerId the provider ID
     * @return List of pending service requests for the provider
     */
    default List<ServiceRequest> findPendingByProviderId(UUID providerId) {
        return findByProviderIdAndStatus(providerId, RequestStatus.PENDING);
    }
}

