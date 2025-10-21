package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.ServiceRequest;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.RequestStatus;
import com.lucknow.healthcare.enums.RequestType;
import com.lucknow.healthcare.enums.Requester;
import com.lucknow.healthcare.repository.ProviderRepository;
import com.lucknow.healthcare.repository.ServiceRepository;
import com.lucknow.healthcare.repository.ServiceRequestRepository;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import com.lucknow.healthcare.service.interfaces.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for ServiceRequest entity operations
 * 
 * Implements business logic for service request management including
 * provider requests, admin approval/rejection, and workflow management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@org.springframework.stereotype.Service
@Transactional
public class ServiceRequestServiceImpl implements ServiceRequestService {
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProviderService providerService;
    
    @Override
    public ServiceRequest requestServiceAddition(UUID providerId, UUID serviceId, String notes) {
        // Validate provider and service
        Provider provider = providerRepository.findById(providerId)
            .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));
        
        com.lucknow.healthcare.entity.Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
        
        // Check if provider already has this service
        if (provider.getServices() != null && provider.getServices().contains(service)) {
            throw new IllegalStateException("Provider already offers this service");
        }
        
        // Check for duplicate pending request
        Optional<ServiceRequest> existingRequest = serviceRequestRepository.findPendingRequest(
            providerId, serviceId, RequestType.ADD, RequestStatus.PENDING
        );
        
        if (existingRequest.isPresent()) {
            throw new IllegalStateException("A pending request already exists for this service");
        }
        
        // Create service request
        ServiceRequest request = new ServiceRequest();
        request.setProvider(provider);
        request.setService(service);
        request.setRequestType(RequestType.ADD);
        request.setRequestedBy(Requester.PROVIDER);
        request.setStatus(RequestStatus.PENDING);
        request.setNotes(notes);
        
        return serviceRequestRepository.save(request);
    }
    
    @Override
    public ServiceRequest requestServiceRemoval(UUID providerId, UUID serviceId, String notes) {
        // Validate provider and service
        Provider provider = providerRepository.findById(providerId)
            .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));
        
        com.lucknow.healthcare.entity.Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
        
        // Check if provider has this service
        if (provider.getServices() == null || !provider.getServices().contains(service)) {
            throw new IllegalStateException("Provider does not offer this service");
        }
        
        // Check for duplicate pending request
        Optional<ServiceRequest> existingRequest = serviceRequestRepository.findPendingRequest(
            providerId, serviceId, RequestType.REMOVE, RequestStatus.PENDING
        );
        
        if (existingRequest.isPresent()) {
            throw new IllegalStateException("A pending request already exists for this service");
        }
        
        // Create service request
        ServiceRequest request = new ServiceRequest();
        request.setProvider(provider);
        request.setService(service);
        request.setRequestType(RequestType.REMOVE);
        request.setRequestedBy(Requester.PROVIDER);
        request.setStatus(RequestStatus.PENDING);
        request.setNotes(notes);
        
        return serviceRequestRepository.save(request);
    }
    
    @Override
    public ServiceRequest approveRequest(UUID requestId, UUID adminId) {
        // Find request
        ServiceRequest request = serviceRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + requestId));
        
        // Find admin user
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with ID: " + adminId));
        
        // Check if request is pending
        if (!request.isPending()) {
            throw new IllegalStateException("Only pending requests can be approved. Current status: " + request.getStatus());
        }
        
        // Execute the service change
        try {
            if (request.getRequestType() == RequestType.ADD) {
                providerService.addServiceToProvider(
                    request.getProvider().getId(),
                    request.getService().getId()
                );
            } else if (request.getRequestType() == RequestType.REMOVE) {
                providerService.removeServiceFromProvider(
                    request.getProvider().getId(),
                    request.getService().getId()
                );
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute service change: " + e.getMessage(), e);
        }
        
        // Update request status
        request.approve(admin);
        
        return serviceRequestRepository.save(request);
    }
    
    @Override
    public ServiceRequest rejectRequest(UUID requestId, UUID adminId, String reason) {
        // Find request
        ServiceRequest request = serviceRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + requestId));
        
        // Find admin user
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin user not found with ID: " + adminId));
        
        // Check if request is pending
        if (!request.isPending()) {
            throw new IllegalStateException("Only pending requests can be rejected. Current status: " + request.getStatus());
        }
        
        // Update request status
        request.reject(admin, reason);
        
        return serviceRequestRepository.save(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getAllPendingRequests() {
        return serviceRequestRepository.findAllPending();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getPendingRequestsByProvider(UUID providerId) {
        return serviceRequestRepository.findPendingByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequest> getAllRequestsByProvider(UUID providerId) {
        return serviceRequestRepository.findByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceRequest> findById(UUID requestId) {
        return serviceRequestRepository.findById(requestId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return serviceRequestRepository.countByStatus(RequestStatus.PENDING);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPendingRequestsByProvider(UUID providerId) {
        return serviceRequestRepository.countByProviderIdAndStatus(providerId, RequestStatus.PENDING);
    }
}

