package com.lucknow.healthcare.dto;

import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Privacy-Aware User DTO for Provider Access
 * 
 * Implements customer privacy protection where providers can only see
 * customer contact details (phone and address) within 24 hours before
 * and after the service date.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public class PrivacyAwareUserDTO {
    
    private UUID id;
    private String name;
    private String email;
    private String phone; // May be null if outside privacy window
    private String address; // May be null if outside privacy window
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean contactDetailsAvailable; // Indicates if phone/address are visible
    private String privacyMessage; // Explains why details are hidden
    
    // Constructors
    public PrivacyAwareUserDTO() {}
    
    /**
     * Create privacy-aware user DTO based on service date and requesting user role
     * 
     * @param userDTO Original user DTO
     * @param serviceDate Date of the service
     * @param requestingUserRole Role of the user requesting the data
     */
    public PrivacyAwareUserDTO(UserDTO userDTO, LocalDate serviceDate, String requestingUserRole) {
        this.id = userDTO.getId();
        this.name = userDTO.getName();
        this.email = userDTO.getEmail();
        this.role = userDTO.getRole();
        this.status = userDTO.getStatus();
        this.emailVerified = userDTO.getEmailVerified();
        this.createdAt = userDTO.getCreatedAt();
        this.updatedAt = userDTO.getUpdatedAt();
        
        // Apply privacy rules only for providers
        if ("PROVIDER".equals(requestingUserRole)) {
            applyPrivacyRules(serviceDate);
        } else {
            // For admins and customers, always show contact details
            this.phone = userDTO.getPhone();
            this.address = userDTO.getAddress();
            this.contactDetailsAvailable = true;
            this.privacyMessage = "Contact details available";
        }
    }
    
    /**
     * Apply privacy rules based on service date
     * Customer contact details are only visible 24 hours before and after service
     */
    private void applyPrivacyRules(LocalDate serviceDate) {
        LocalDate today = LocalDate.now();
        LocalDate dayBefore = serviceDate.minusDays(1);
        LocalDate dayAfter = serviceDate.plusDays(1);
        
        // Check if current date is within privacy window
        boolean isWithinPrivacyWindow = !today.isBefore(dayBefore) && !today.isAfter(dayAfter);
        
        if (isWithinPrivacyWindow) {
            // Within privacy window - show contact details
            this.phone = null; // Will be set from original DTO
            this.address = null; // Will be set from original DTO
            this.contactDetailsAvailable = true;
            this.privacyMessage = "Contact details available for service on " + serviceDate;
        } else {
            // Outside privacy window - hide contact details
            this.phone = null;
            this.address = null;
            this.contactDetailsAvailable = false;
            
            if (today.isBefore(dayBefore)) {
                this.privacyMessage = "Contact details will be available 24 hours before service";
            } else {
                this.privacyMessage = "Contact details are no longer available (expired 24 hours after service)";
            }
        }
    }
    
    /**
     * Set contact details (only if within privacy window)
     */
    public void setContactDetails(String phone, String address) {
        if (this.contactDetailsAvailable) {
            this.phone = phone;
            this.address = address;
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getContactDetailsAvailable() {
        return contactDetailsAvailable;
    }
    
    public void setContactDetailsAvailable(Boolean contactDetailsAvailable) {
        this.contactDetailsAvailable = contactDetailsAvailable;
    }
    
    public String getPrivacyMessage() {
        return privacyMessage;
    }
    
    public void setPrivacyMessage(String privacyMessage) {
        this.privacyMessage = privacyMessage;
    }
}
