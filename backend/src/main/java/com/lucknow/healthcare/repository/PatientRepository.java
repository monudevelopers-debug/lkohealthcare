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
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND p.isActive = true")
    Page<Patient> findByCustomerIdAndIsActiveTrue(@Param("customerId") UUID customerId, Pageable pageable);
    
    /**
     * Find all patients for a specific customer (including inactive)
     * 
     * @param customerId the customer's user ID
     * @param pageable pagination information
     * @return page of all patients
     */
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId")
    Page<Patient> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
    
    /**
     * Find all active patients for a specific customer (list)
     * 
     * @param customerId the customer's user ID
     * @return list of active patients
     */
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND p.isActive = true")
    List<Patient> findByCustomerIdAndIsActiveTrue(@Param("customerId") UUID customerId);
    
    /**
     * Find patient by ID and customer ID (security check)
     * 
     * @param id the patient ID
     * @param customerId the customer's user ID
     * @return optional patient
     */
    @Query("SELECT p FROM Patient p WHERE p.id = :id AND p.customer.id = :customerId")
    Optional<Patient> findByIdAndCustomerId(@Param("id") UUID id, @Param("customerId") UUID customerId);
    
    /**
     * Find patient by ID and customer ID for active patients only
     * 
     * @param id the patient ID
     * @param customerId the customer's user ID
     * @return optional patient
     */
    @Query("SELECT p FROM Patient p WHERE p.id = :id AND p.customer.id = :customerId AND p.isActive = true")
    Optional<Patient> findByIdAndCustomerIdAndIsActiveTrue(@Param("id") UUID id, @Param("customerId") UUID customerId);
    
    /**
     * Count active patients for a customer
     * 
     * @param customerId the customer's user ID
     * @return count of active patients
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.customer.id = :customerId AND p.isActive = true")
    long countByCustomerIdAndIsActiveTrue(@Param("customerId") UUID customerId);
    
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
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND p.isSensitiveData = true")
    List<Patient> findByCustomerIdAndIsSensitiveDataTrue(@Param("customerId") UUID customerId);
    
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
    @Query("SELECT p FROM Patient p WHERE p.customer.id = :customerId AND p.relationshipToCustomer = :relationship AND p.isActive = true")
    List<Patient> findByCustomerIdAndRelationshipToCustomerAndIsActiveTrue(
        @Param("customerId") UUID customerId, 
        @Param("relationship") Patient.Relationship relationship
    );
    
    /**
     * Check if a patient exists for a customer
     * 
     * @param id patient ID
     * @param customerId customer ID
     * @return true if patient exists and belongs to customer
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Patient p WHERE p.id = :id AND p.customer.id = :customerId")
    boolean existsByIdAndCustomerId(@Param("id") UUID id, @Param("customerId") UUID customerId);
}

