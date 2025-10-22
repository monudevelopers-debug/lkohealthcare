package com.lucknow.healthcare.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.repository.BookingRepository;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

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
    void testGetBookings_WithValidToken_ReturnsBookings() throws Exception {
        // Create test booking
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setStatus(BookingStatus.PENDING);
        booking.setScheduledDate(LocalDate.now().plusDays(1));
        booking.setScheduledTime(LocalTime.of(10, 0));
        booking.setTotalAmount(BigDecimal.valueOf(100.0));
        bookingRepository.save(booking);

        mockMvc.perform(get("/bookings")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void testGetBookings_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/bookings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateBooking_WithValidData_ReturnsCreated() throws Exception {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);
        booking.setScheduledDate(LocalDate.now().plusDays(1));
        booking.setScheduledTime(LocalTime.of(10, 0));
        booking.setTotalAmount(BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/bookings")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testUpdateBookingStatus_WithValidStatus_ReturnsOk() throws Exception {
        // Create test booking
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setStatus(BookingStatus.PENDING);
        booking.setScheduledDate(LocalDate.now().plusDays(1));
        booking.setScheduledTime(LocalTime.of(10, 0));
        booking.setTotalAmount(BigDecimal.valueOf(100.0));
        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/bookings/" + booking.getId() + "/status")
                .header("Authorization", "Bearer " + authToken)
                .param("status", "CONFIRMED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testGetBookingById_WithValidId_ReturnsBooking() throws Exception {
        // Create test booking
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setStatus(BookingStatus.PENDING);
        booking.setScheduledDate(LocalDate.now().plusDays(1));
        booking.setScheduledTime(LocalTime.of(10, 0));
        booking.setTotalAmount(BigDecimal.valueOf(100.0));
        booking = bookingRepository.save(booking);

        mockMvc.perform(get("/bookings/" + booking.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetBookingById_WithInvalidId_ReturnsNotFound() throws Exception {
        UUID invalidId = UUID.randomUUID();

        mockMvc.perform(get("/bookings/" + invalidId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCancelBooking_WithValidId_ReturnsOk() throws Exception {
        // Create test booking
        Booking booking = new Booking();
        booking.setUser(testUser);
        booking.setStatus(BookingStatus.PENDING);
        booking.setScheduledDate(LocalDate.now().plusDays(1));
        booking.setScheduledTime(LocalTime.of(10, 0));
        booking.setTotalAmount(BigDecimal.valueOf(100.0));
        booking = bookingRepository.save(booking);

        mockMvc.perform(put("/bookings/" + booking.getId() + "/cancel")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
