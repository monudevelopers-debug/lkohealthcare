package com.lucknow.healthcare.dto;

import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Privacy-Aware Booking DTO for Provider Access
 * 
 * Implements customer privacy protection where providers can only see
 * customer contact details within 24 hours before and after the service date.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public class PrivacyAwareBookingDTO {
    
    private UUID id;
    private PrivacyAwareUserDTO user;
    private ServiceDTO service;
    private ProviderDTO provider;
    private PrivacyAwarePatientDTO patient;
    private BookingStatus status;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private Integer duration;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String specialInstructions;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean customerContactAvailable; // Indicates if customer contact is visible
    private String privacyMessage; // Explains privacy status
    
    // Constructors
    public PrivacyAwareBookingDTO() {}
    
    /**
     * Create privacy-aware booking DTO
     * 
     * @param bookingDTO Original booking DTO
     * @param requestingUserRole Role of the user requesting the data
     */
    public PrivacyAwareBookingDTO(BookingDTO bookingDTO, String requestingUserRole) {
        this.id = bookingDTO.getId();
        this.service = bookingDTO.getService();
        this.provider = bookingDTO.getProvider();
        this.status = bookingDTO.getStatus();
        this.scheduledDate = bookingDTO.getScheduledDate();
        this.scheduledTime = bookingDTO.getScheduledTime();
        this.duration = bookingDTO.getDuration();
        this.totalAmount = bookingDTO.getTotalAmount();
        this.paymentStatus = bookingDTO.getPaymentStatus();
        this.specialInstructions = bookingDTO.getSpecialInstructions();
        this.notes = bookingDTO.getNotes();
        this.createdAt = bookingDTO.getCreatedAt();
        this.updatedAt = bookingDTO.getUpdatedAt();
        
        // Create privacy-aware user and patient DTOs with role check
        if (bookingDTO.getUser() != null) {
            this.user = new PrivacyAwareUserDTO(bookingDTO.getUser(), this.scheduledDate, requestingUserRole);
        }
        
        if (bookingDTO.getPatient() != null) {
            this.patient = new PrivacyAwarePatientDTO(bookingDTO.getPatient(), this.scheduledDate, requestingUserRole);
        }
        
        // Set overall privacy status
        this.customerContactAvailable = (this.user != null && this.user.getContactDetailsAvailable()) ||
                                      (this.patient != null && this.patient.getEmergencyContactAvailable());
        
        // Set privacy message
        if (this.user != null && this.user.getPrivacyMessage() != null) {
            this.privacyMessage = this.user.getPrivacyMessage();
        } else if (this.patient != null && this.patient.getPrivacyMessage() != null) {
            this.privacyMessage = this.patient.getPrivacyMessage();
        } else {
            this.privacyMessage = "Customer contact details are available";
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public PrivacyAwareUserDTO getUser() {
        return user;
    }
    
    public void setUser(PrivacyAwareUserDTO user) {
        this.user = user;
    }
    
    public ServiceDTO getService() {
        return service;
    }
    
    public void setService(ServiceDTO service) {
        this.service = service;
    }
    
    public ProviderDTO getProvider() {
        return provider;
    }
    
    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }
    
    public PrivacyAwarePatientDTO getPatient() {
        return patient;
    }
    
    public void setPatient(PrivacyAwarePatientDTO patient) {
        this.patient = patient;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public LocalTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public Boolean getCustomerContactAvailable() {
        return customerContactAvailable;
    }
    
    public void setCustomerContactAvailable(Boolean customerContactAvailable) {
        this.customerContactAvailable = customerContactAvailable;
    }
    
    public String getPrivacyMessage() {
        return privacyMessage;
    }
    
    public void setPrivacyMessage(String privacyMessage) {
        this.privacyMessage = privacyMessage;
    }
}
