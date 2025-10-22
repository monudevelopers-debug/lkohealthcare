package com.lucknow.healthcare.dto;

import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Data Transfer Object for Booking entity
 * 
 * Provides a simplified representation of booking data for API responses,
 * avoiding complex entity relationships and lazy loading issues.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public class BookingDTO {
    
    private UUID id;
    private UserDTO user;
    private ServiceDTO service;
    private ProviderDTO provider;
    private PatientDTO patient;
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
    
    // Constructors
    public BookingDTO() {}
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UserDTO getUser() {
        return user;
    }
    
    public void setUser(UserDTO user) {
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
    
    public PatientDTO getPatient() {
        return patient;
    }
    
    public void setPatient(PatientDTO patient) {
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
}
