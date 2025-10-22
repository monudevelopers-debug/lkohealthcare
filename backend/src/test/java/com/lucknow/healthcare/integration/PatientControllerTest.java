package com.lucknow.healthcare.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Patient;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.repository.PatientRepository;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser.setRole(UserRole.CUSTOMER);
        testUser = userRepository.save(testUser);

        // Generate JWT token
        authToken = jwtUtil.generateToken(testUser.getEmail());
    }

    @Test
    void testGetPatients_WithValidToken_ReturnsPatients() throws Exception {
        // Create test patient
        Patient patient = new Patient();
        patient.setCustomer(testUser);
        patient.setName("John Doe");
        patient.setAge(34);
        patient.setGender(Patient.Gender.MALE);
        patient.setRelationshipToCustomer(Patient.Relationship.SELF);
        patientRepository.save(patient);

        mockMvc.perform(get("/patients")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void testGetPatients_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/patients")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreatePatient_WithValidData_ReturnsCreated() throws Exception {
        Patient patient = new Patient();
        patient.setName("Jane Smith");
        patient.setAge(39);
        patient.setGender(Patient.Gender.FEMALE);
        patient.setRelationshipToCustomer(Patient.Relationship.SPOUSE);

        mockMvc.perform(post("/patients")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Smith"));
    }

    @Test
    void testCreatePatient_WithInvalidData_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();
        // Missing required fields

        mockMvc.perform(post("/patients")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPatientById_WithValidId_ReturnsPatient() throws Exception {
        // Create test patient
        Patient patient = new Patient();
        patient.setCustomer(testUser);
        patient.setName("John Doe");
        patient.setAge(34);
        patient.setGender(Patient.Gender.MALE);
        patient.setRelationshipToCustomer(Patient.Relationship.SELF);
        patient = patientRepository.save(patient);

        mockMvc.perform(get("/patients/" + patient.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patient.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testUpdatePatient_WithValidData_ReturnsOk() throws Exception {
        // Create test patient
        Patient patient = new Patient();
        patient.setCustomer(testUser);
        patient.setName("John Doe");
        patient.setAge(34);
        patient.setGender(Patient.Gender.MALE);
        patient.setRelationshipToCustomer(Patient.Relationship.SELF);
        patient = patientRepository.save(patient);

        // Update patient
        patient.setName("Updated John Doe");

        mockMvc.perform(put("/patients/" + patient.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated John Doe"));
    }

    @Test
    void testDeletePatient_WithValidId_ReturnsNoContent() throws Exception {
        // Create test patient
        Patient patient = new Patient();
        patient.setCustomer(testUser);
        patient.setName("John Doe");
        patient.setAge(34);
        patient.setGender(Patient.Gender.MALE);
        patient.setRelationshipToCustomer(Patient.Relationship.SELF);
        patient = patientRepository.save(patient);

        mockMvc.perform(delete("/patients/" + patient.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testSearchPatients_WithValidQuery_ReturnsFilteredResults() throws Exception {
        // Create test patients
        Patient patient1 = new Patient();
        patient1.setCustomer(testUser);
        patient1.setName("John Doe");
        patient1.setAge(34);
        patient1.setGender(Patient.Gender.MALE);
        patient1.setRelationshipToCustomer(Patient.Relationship.SELF);
        patientRepository.save(patient1);

        Patient patient2 = new Patient();
        patient2.setCustomer(testUser);
        patient2.setName("Jane Smith");
        patient2.setAge(39);
        patient2.setGender(Patient.Gender.FEMALE);
        patient2.setRelationshipToCustomer(Patient.Relationship.SPOUSE);
        patientRepository.save(patient2);

        mockMvc.perform(get("/patients/search")
                .header("Authorization", "Bearer " + authToken)
                .param("name", "John")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }
}
