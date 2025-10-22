package com.lucknow.healthcare.mapper;

import com.lucknow.healthcare.dto.*;
import com.lucknow.healthcare.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper service for converting entities to DTOs
 * 
 * Handles the conversion between complex JPA entities and simplified DTOs
 * to avoid lazy loading issues and provide clean API responses.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Component
public class BookingMapper {
    
    /**
     * Convert Booking entity to BookingDTO
     */
    public BookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUser(toUserDTO(booking.getUser()));
        dto.setService(toServiceDTO(booking.getService()));
        dto.setProvider(toProviderDTO(booking.getProvider()));
        dto.setPatient(toPatientDTO(booking.getPatient()));
        dto.setStatus(booking.getStatus());
        dto.setScheduledDate(booking.getScheduledDate());
        dto.setScheduledTime(booking.getScheduledTime());
        dto.setDuration(booking.getDuration());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setSpecialInstructions(booking.getSpecialInstructions());
        dto.setNotes(booking.getNotes());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert list of Booking entities to list of BookingDTOs
     */
    public List<BookingDTO> toDTOList(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        
        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert User entity to UserDTO
     */
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert Service entity to ServiceDTO
     */
    public ServiceDTO toServiceDTO(Service service) {
        if (service == null) {
            return null;
        }
        
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setPrice(service.getPrice());
        dto.setDuration(service.getDuration());
        dto.setIsActive(service.getIsActive());
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());
        
        // Map category if it exists
        if (service.getCategory() != null) {
            ServiceCategoryDTO categoryDTO = new ServiceCategoryDTO(
                service.getCategory().getId(),
                service.getCategory().getName(),
                service.getCategory().getDescription(),
                null, // imageUrl not available in entity
                service.getCategory().getCreatedAt(),
                service.getCategory().getUpdatedAt()
            );
            dto.setCategory(categoryDTO);
        }
        
        return dto;
    }
    
    /**
     * Convert Provider entity to ProviderDTO
     */
    public ProviderDTO toProviderDTO(Provider provider) {
        if (provider == null) {
            return null;
        }
        
        ProviderDTO dto = new ProviderDTO();
        dto.setId(provider.getId());
        dto.setName(provider.getName());
        dto.setEmail(provider.getEmail());
        dto.setPhone(provider.getPhone());
        dto.setQualification(provider.getQualification());
        dto.setExperience(provider.getExperience());
        dto.setAvailabilityStatus(provider.getAvailabilityStatus());
        dto.setRating(provider.getRating());
        dto.setTotalRatings(provider.getTotalRatings());
        dto.setIsVerified(provider.getIsVerified());
        dto.setDocuments(provider.getDocuments());
        dto.setCreatedAt(provider.getCreatedAt());
        dto.setUpdatedAt(provider.getUpdatedAt());
        
        // Convert services if available (avoid lazy loading)
        if (provider.getServices() != null) {
            dto.setServices(provider.getServices().stream()
                    .map(this::toServiceDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convert Patient entity to PatientDTO
     */
    public PatientDTO toPatientDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setCustomerId(patient.getCustomerId());
        dto.setName(patient.getName());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender());
        dto.setWeight(patient.getWeight());
        dto.setHeight(patient.getHeight());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setIsDiabetic(patient.getIsDiabetic());
        dto.setBpStatus(patient.getBpStatus());
        dto.setAllergies(patient.getAllergies());
        dto.setChronicConditions(patient.getChronicConditions());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        dto.setEmergencyContactRelation(patient.getEmergencyContactRelation());
        dto.setRelationshipToCustomer(patient.getRelationshipToCustomer());
        dto.setIsSensitiveData(patient.getIsSensitiveData());
        dto.setIsActive(patient.getIsActive());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        
        return dto;
    }
}
