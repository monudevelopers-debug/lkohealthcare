package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.ServiceRequest;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.service.interfaces.ServiceRequestService;
import com.lucknow.healthcare.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for ServiceRequest operations
 * 
 * Provides REST endpoints for service request management including
 * provider requests, admin approval/rejection, and workflow management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/service-requests")
@CrossOrigin(origins = "*")
public class ServiceRequestController {
    
    @Autowired
    private ServiceRequestService serviceRequestService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Provider requests to add a service
     * 
     * @param providerId the provider ID
     * @param serviceId the service ID
     * @param requestBody optional request body with notes
     * @return ResponseEntity containing the created service request
     */
    @PostMapping("/providers/{providerId}/services/{serviceId}/request-add")
    public ResponseEntity<?> requestServiceAddition(
            @PathVariable UUID providerId,
            @PathVariable UUID serviceId,
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String notes = requestBody != null ? requestBody.get("notes") : null;
            ServiceRequest request = serviceRequestService.requestServiceAddition(providerId, serviceId, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create service request: " + e.getMessage()));
        }
    }
    
    /**
     * Provider requests to remove a service
     * 
     * @param providerId the provider ID
     * @param serviceId the service ID
     * @param requestBody optional request body with notes
     * @return ResponseEntity containing the created service request
     */
    @PostMapping("/providers/{providerId}/services/{serviceId}/request-remove")
    public ResponseEntity<?> requestServiceRemoval(
            @PathVariable UUID providerId,
            @PathVariable UUID serviceId,
            @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            String notes = requestBody != null ? requestBody.get("notes") : null;
            ServiceRequest request = serviceRequestService.requestServiceRemoval(providerId, serviceId, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create service request: " + e.getMessage()));
        }
    }
    
    /**
     * Get all pending service requests (for admin review)
     * 
     * @return ResponseEntity containing list of pending requests
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingRequests() {
        try {
            List<ServiceRequest> requests = serviceRequestService.getAllPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch pending requests: " + e.getMessage()));
        }
    }
    
    /**
     * Get pending requests for a specific provider
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing list of pending requests
     */
    @GetMapping("/providers/{providerId}/pending")
    public ResponseEntity<?> getPendingRequestsByProvider(@PathVariable UUID providerId) {
        try {
            List<ServiceRequest> requests = serviceRequestService.getPendingRequestsByProvider(providerId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch provider requests: " + e.getMessage()));
        }
    }
    
    /**
     * Get all requests for a specific provider (pending + history)
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing list of all requests
     */
    @GetMapping("/providers/{providerId}")
    public ResponseEntity<?> getAllRequestsByProvider(@PathVariable UUID providerId) {
        try {
            List<ServiceRequest> requests = serviceRequestService.getAllRequestsByProvider(providerId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch provider requests: " + e.getMessage()));
        }
    }
    
    /**
     * Admin approves a service request
     * 
     * @param requestId the request ID
     * @return ResponseEntity containing the approved request
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable UUID requestId) {
        try {
            // Get current authenticated user (admin)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> adminOpt = userService.findByEmail(email);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Admin user not found"));
            }
            
            User admin = adminOpt.get();
            ServiceRequest approvedRequest = serviceRequestService.approveRequest(requestId, admin.getId());
            
            return ResponseEntity.ok(approvedRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to approve request: " + e.getMessage()));
        }
    }
    
    /**
     * Admin rejects a service request
     * 
     * @param requestId the request ID
     * @param requestBody request body containing rejection reason
     * @return ResponseEntity containing the rejected request
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable UUID requestId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String reason = requestBody.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rejection reason is required"));
            }
            
            // Get current authenticated user (admin)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> adminOpt = userService.findByEmail(email);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Admin user not found"));
            }
            
            User admin = adminOpt.get();
            ServiceRequest rejectedRequest = serviceRequestService.rejectRequest(requestId, admin.getId(), reason);
            
            return ResponseEntity.ok(rejectedRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to reject request: " + e.getMessage()));
        }
    }
    
    /**
     * Get service request by ID
     * 
     * @param requestId the request ID
     * @return ResponseEntity containing the request
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(@PathVariable UUID requestId) {
        try {
            Optional<ServiceRequest> requestOpt = serviceRequestService.findById(requestId);
            if (requestOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(requestOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch request: " + e.getMessage()));
        }
    }
    
    /**
     * Count pending requests
     * 
     * @return ResponseEntity containing the count
     */
    @GetMapping("/pending/count")
    public ResponseEntity<?> countPendingRequests() {
        try {
            long count = serviceRequestService.countPendingRequests();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to count pending requests: " + e.getMessage()));
        }
    }
    
    /**
     * Count pending requests for a provider
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing the count
     */
    @GetMapping("/providers/{providerId}/pending/count")
    public ResponseEntity<?> countPendingRequestsByProvider(@PathVariable UUID providerId) {
        try {
            long count = serviceRequestService.countPendingRequestsByProvider(providerId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to count pending requests: " + e.getMessage()));
        }
    }
}

