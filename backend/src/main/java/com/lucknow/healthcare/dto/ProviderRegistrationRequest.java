package com.lucknow.healthcare.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO for provider registration request
 * 
 * Contains all necessary information to create a new provider account
 * including personal details, credentials, and verification documents.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public class ProviderRegistrationRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Phone is required")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phone;
    
    @NotBlank(message = "Qualification is required")
    @Size(max = 255, message = "Qualification must not exceed 255 characters")
    private String qualification;
    
    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experience;
    
    @Size(max = 10, message = "Maximum 10 documents allowed")
    private List<String> documents;
    
    // Constructors
    public ProviderRegistrationRequest() {}
    
    public ProviderRegistrationRequest(String name, String email, String password, String phone, 
                                     String qualification, Integer experience, List<String> documents) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.qualification = qualification;
        this.experience = experience;
        this.documents = documents;
    }
    
    // Getters and Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    public Integer getExperience() {
        return experience;
    }
    
    public void setExperience(Integer experience) {
        this.experience = experience;
    }
    
    public List<String> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }
}
