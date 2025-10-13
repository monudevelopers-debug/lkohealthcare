package com.lucknow.healthcare.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.repository.BookingRepository;
import com.lucknow.healthcare.repository.ProviderRepository;
import com.lucknow.healthcare.repository.ServiceRepository;
import com.lucknow.healthcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for booking management
 * 
 * Tests the complete booking workflow from creation to completion
 * including provider assignment and status updates.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class BookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProviderRepository providerRepository;

    private User testUser;
    private Provider testProvider;
    private Service testService;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Test Customer");
        testUser.setEmail("customer@test.com");
        testUser.setPhone("+91-9876543210");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser = userRepository.save(testUser);

        // Create test provider
        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setName("Test Provider");
        testProvider.setEmail("provider@test.com");
        testProvider.setPhone("+91-9876543211");
        testProvider.setQualification("MBBS, MD");
        testProvider.setExperience(5);
        testProvider.setRating(4.5);
        testProvider.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        testProvider = providerRepository.save(testProvider);

        // Create test service
        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Nursing Care");
        testService.setDescription("Professional nursing care services");
        testService.setPrice(new BigDecimal("500.00"));
        testService.setDuration(60);
        testService.setIsActive(true);
        testService = serviceRepository.save(testService);

        // Create test booking
        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setUser(testUser);
        testBooking.setService(testService);
        testBooking.setScheduledDate(LocalDate.now().plusDays(1));
        testBooking.setScheduledTime(LocalTime.of(10, 0));
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setTotalAmount(new BigDecimal("500.00"));
        testBooking.setNotes("Test booking");
        testBooking = bookingRepository.save(testBooking);
    }

    @Test
    void createBooking_ValidData_ReturnsCreatedBooking() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setUser(testUser);
        newBooking.setService(testService);
        newBooking.setScheduledDate(LocalDate.now().plusDays(2));
        newBooking.setScheduledTime(LocalTime.of(14, 0));
        newBooking.setTotalAmount(new BigDecimal("500.00"));
        newBooking.setNotes("New test booking");

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.service.id").value(testService.getId().toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.toString()))
                .andExpect(jsonPath("$.totalAmount").value(500.00));
    }

    @Test
    void getBookingById_ExistingBooking_ReturnsBooking() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", testBooking.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$.user.name").value(testUser.getName()))
                .andExpect(jsonPath("$.service.name").value(testService.getName()))
                .andExpect(jsonPath("$.status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void getBookingById_NonExistentBooking_ReturnsNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingStatus_ValidStatus_ReturnsUpdatedBooking() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/status", testBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.toString()));
    }

    @Test
    void assignProvider_ValidProvider_ReturnsUpdatedBooking() throws Exception {
        // Given
        String providerId = testProvider.getId().toString();

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/assign", testBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"providerId\":\"" + providerId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$.provider.id").value(providerId));
    }

    @Test
    void getBookingsByUser_ValidUser_ReturnsUserBookings() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$[0].user.id").value(testUser.getId().toString()));
    }

    @Test
    void getBookingsByProvider_ValidProvider_ReturnsProviderBookings() throws Exception {
        // Given - Assign provider to booking first
        testBooking.setProvider(testProvider);
        bookingRepository.save(testBooking);

        // When & Then
        mockMvc.perform(get("/api/bookings/provider/{providerId}", testProvider.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$[0].provider.id").value(testProvider.getId().toString()));
    }

    @Test
    void getBookingsByStatus_ValidStatus_ReturnsFilteredBookings() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/status/{status}", BookingStatus.PENDING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value(BookingStatus.PENDING.toString()));
    }

    @Test
    void updateBooking_ValidData_ReturnsUpdatedBooking() throws Exception {
        // Given
        Booking updatedBooking = new Booking();
        updatedBooking.setId(testBooking.getId());
        updatedBooking.setUser(testUser);
        updatedBooking.setService(testService);
        updatedBooking.setScheduledDate(LocalDate.now().plusDays(3));
        updatedBooking.setScheduledTime(LocalTime.of(16, 0));
        updatedBooking.setStatus(BookingStatus.CONFIRMED);
        updatedBooking.setTotalAmount(new BigDecimal("500.00"));
        updatedBooking.setNotes("Updated booking notes");

        // When & Then
        mockMvc.perform(put("/api/bookings/{id}", testBooking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$.scheduledDate").value(LocalDate.now().plusDays(3).toString()))
                .andExpect(jsonPath("$.scheduledTime").value("16:00:00"))
                .andExpect(jsonPath("$.status").value(BookingStatus.CONFIRMED.toString()))
                .andExpect(jsonPath("$.notes").value("Updated booking notes"));
    }

    @Test
    void deleteBooking_ExistingBooking_ReturnsNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/bookings/{id}", testBooking.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBooking_NonExistentBooking_ReturnsNotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/bookings/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}
