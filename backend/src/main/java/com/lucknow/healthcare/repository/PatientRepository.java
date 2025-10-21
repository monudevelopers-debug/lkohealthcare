package com.lucknow.healthcare.repository;

import com.lucknow.healthcare.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Patient entity
 * 
 * Provides data access methods for patient management including
 * CRUD operations, customer-based queries, and filtering.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    
    /**
     * Find all active patients for a specific customer
     * 
     * @param customerId the customer's user ID
     * @param pageable pagination information
     * @return page of active patients
     */
    Page<Patient> findByCustomerIdAndIsActiveTrue(UUID customerId, Pageable pageable);
    
    /**
     * Find all patients for a specific customer (including inactive)
     * 
     * @param customerId the customer's user ID
     * @param pageable pagination information
     * @return page of all patients
     */
    Page<Patient> findByCustomerId(UUID customerId, Pageable pageable);
    
    /**
     * Find all active patients for a specific customer (list)
     * 
     * @param customerId the customer's user ID
     * @return list of active patients
     */
    List<Patient> findByCustomerIdAndIsActiveTrue(UUID customerId);
    
    /**
     * Find patient by ID and customer ID (security check)
     * 
     * @param id the patient ID
     * @param customerId the customer's user ID
     * @return optional patient
     */
    Optional<Patient> findByIdAndCustomerId(UUID id, UUID customerId);
    
    /**
     * Find patient by ID and customer ID for active patients only
     * 
     * @param id the patient ID
     * @param customerId the customer's user ID
     * @return optional patient
     */
    Optional<Patient> findByIdAndCustomerIdAndIsActiveTrue(UUID id, UUID customerId);
    
    /**
     * Count active patients for a customer
     * 
     * @param customerId the customer's user ID
     * @return count of active patients
     */
    long countByCustomerIdAndIsActiveTrue(UUID customerId);
    
    /**
     * Find patients with medical conditions for a customer
     * 
     * @param customerId the customer's user ID
     * @return list of patients with medical conditions
     */
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND " +
           "(p.isDiabetic = true OR p.bpStatus != 'NORMAL' OR " +
           "p.allergies IS NOT NULL OR p.chronicConditions IS NOT NULL)")
    List<Patient> findPatientsWithMedicalConditions(@Param("customerId") UUID customerId);
    
    /**
     * Find minors (age < 18) for a customer
     * 
     * @param customerId the customer's user ID
     * @return list of minor patients
     */
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND p.age < 18 AND p.isActive = true")
    List<Patient> findMinorPatients(@Param("customerId") UUID customerId);
    
    /**
     * Find patients marked as sensitive data
     * 
     * @param customerId the customer's user ID
     * @return list of patients with sensitive data flag
     */
    List<Patient> findByCustomerIdAndIsSensitiveDataTrue(UUID customerId);
    
    /**
     * Search patients by name for a customer
     * 
     * @param customerId the customer's user ID
     * @param name search term for name (case-insensitive)
     * @param pageable pagination information
     * @return page of matching patients
     */
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true")
    Page<Patient> searchByName(@Param("customerId") UUID customerId, 
                                @Param("name") String name, 
                                Pageable pageable);
    
    /**
     * Find patients by relationship type
     * 
     * @param customerId the customer's user ID
     * @param relationship the relationship type
     * @return list of patients with specified relationship
     */
    List<Patient> findByCustomerIdAndRelationshipToCustomerAndIsActiveTrue(
        UUID customerId, 
        Patient.Relationship relationship
    );
    
    /**
     * Check if a patient exists for a customer
     * 
     * @param id patient ID
     * @param customerId customer ID
     * @return true if patient exists and belongs to customer
     */
    boolean existsByIdAndCustomerId(UUID id, UUID customerId);
}

