package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.BookingRejectionRequest;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.service.interfaces.BookingRejectionService;
import com.lucknow.healthcare.service.interfaces.BookingService;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import com.lucknow.healthcare.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for BookingRejectionRequest operations
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/booking-rejections")
@CrossOrigin(origins = "*")
public class BookingRejectionController {
    
    @Autowired
    private BookingRejectionService rejectionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private BookingService bookingService;
    
    /**
     * Provider requests to reject a booking
     */
    @PostMapping("/bookings/{bookingId}/request")
    public ResponseEntity<?> requestRejection(
            @PathVariable UUID bookingId,
            @RequestBody Map<String, String> body) {
        try {
            String reason = body.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Rejection reason is required"));
            }
            
            // Get current provider ID from authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // Get provider from authentication
            com.lucknow.healthcare.entity.Provider provider = providerService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
            
            BookingRejectionRequest request = rejectionService.requestBookingRejection(
                bookingId,
                provider.getId(),
                reason
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create rejection request: " + e.getMessage()));
        }
    }
    
    /**
     * Get all pending rejection requests (admin)
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getAllPending() {
        try {
            List<BookingRejectionRequest> requests = rejectionService.getAllPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Admin approves rejection (booking gets cancelled and unassigned)
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approveRejection(
            @PathVariable UUID requestId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String adminNotes = body != null ? body.get("notes") : null;
            
            // Get current admin
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> adminOpt = userService.findByEmail(email);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Admin not found"));
            }
            
            BookingRejectionRequest approved = rejectionService.approveRejection(
                requestId, 
                adminOpt.get().getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(approved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to approve rejection: " + e.getMessage()));
        }
    }
    
    /**
     * Admin denies rejection (provider must complete booking)
     */
    @PostMapping("/{requestId}/deny")
    public ResponseEntity<?> denyRejection(
            @PathVariable UUID requestId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String adminNotes = body != null ? body.get("notes") : "Provider must complete this booking.";
            
            // Get current admin
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<User> adminOpt = userService.findByEmail(email);
            if (adminOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Admin not found"));
            }
            
            BookingRejectionRequest denied = rejectionService.denyRejection(
                requestId, 
                adminOpt.get().getId(), 
                adminNotes
            );
            
            return ResponseEntity.ok(denied);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to deny rejection: " + e.getMessage()));
        }
    }
    
    /**
     * Count pending rejection requests
     */
    @GetMapping("/pending/count")
    public ResponseEntity<?> countPending() {
        try {
            long count = rejectionService.countPendingRequests();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}

