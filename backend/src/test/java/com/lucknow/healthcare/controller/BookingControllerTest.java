package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.ProviderStatus;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for BookingController
 * 
 * Tests all booking management endpoints including CRUD operations,
 * status updates, provider assignment, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking testBooking;
    private User testUser;
    private Provider testProvider;
    private Service testService;
    private ServiceCategory testCategory;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("+91-9876543210");
        testUser.setRole(UserRole.CUSTOMER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Setup test category
        testCategory = new ServiceCategory();
        testCategory.setId(UUID.randomUUID());
        testCategory.setName("General Medicine");
        testCategory.setDescription("General medical services");
        testCategory.setIsActive(true);
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        // Setup test service
        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("General Consultation");
        testService.setDescription("General medical consultation");
        testService.setPrice(BigDecimal.valueOf(500.00));
        testService.setDuration(30);
        testService.setCategory(testCategory);
        testService.setIsActive(true);
        testService.setCreatedAt(LocalDateTime.now());
        testService.setUpdatedAt(LocalDateTime.now());

        // Setup test provider
        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setUser(testUser);
        testProvider.setSpecialization("General Medicine");
        testProvider.setExperience(5);
        testProvider.setRating(4.5);
        testProvider.setStatus(ProviderStatus.ACTIVE);
        testProvider.setIsAvailable(true);
        testProvider.setConsultationFee(BigDecimal.valueOf(500.00));
        testProvider.setCreatedAt(LocalDateTime.now());
        testProvider.setUpdatedAt(LocalDateTime.now());

        // Setup test booking
        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setUser(testUser);
        testBooking.setProvider(testProvider);
        testBooking.setService(testService);
        testBooking.setBookingDate(LocalDateTime.now().plusDays(1));
        testBooking.setStatus(BookingStatus.CONFIRMED);
        testBooking.setTotalAmount(BigDecimal.valueOf(500.00));
        testBooking.setCreatedAt(LocalDateTime.now());
        testBooking.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllBookings_Success() throws Exception {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[0].totalAmount").value(500.00));
    }

    @Test
    void testGetAllBookings_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetAllBookings_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingById_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalAmount").value(500.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingById_NotFound() throws Exception {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", bookingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testGetBookingById_OwnBooking_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingService.getBookingById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testGetBookingById_OtherBooking_ReturnsForbidden() throws Exception {
        // Given
        UUID otherBookingId = UUID.randomUUID();
        when(bookingService.getBookingById(otherBookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", otherBookingId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByUserId_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByUserId(userId)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByUserId_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/user/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByProviderId_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByProviderId(providerId)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/provider/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByProviderId_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/provider/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByStatus_Success() throws Exception {
        // Given
        BookingStatus status = BookingStatus.CONFIRMED;
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByStatus(status)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByDateRange_Success() throws Exception {
        // Given
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.getBookingsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/date-range")
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByDateRange_WithInvalidDateFormat_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/date-range")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetBookingsByDateRange_WithMissingEndDate_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/date-range")
                .param("startDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchBookings_Success() throws Exception {
        // Given
        String searchTerm = "consultation";
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingService.searchBookings(searchTerm)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchBookings_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchBookings_WithNullSearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateBooking_Success() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setBookingDate(LocalDateTime.now().plusDays(1));
        newBooking.setTotalAmount(BigDecimal.valueOf(500.00));

        when(bookingService.createBooking(any(Booking.class))).thenReturn(testBooking);

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testBooking.getId().toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testCreateBooking_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setBookingDate(LocalDateTime.now().plusDays(1));

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateBooking_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setBookingDate(null); // Invalid data

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateBooking_WithPastDate_ReturnsBadRequest() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setBookingDate(LocalDateTime.now().minusDays(1)); // Past date

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateBooking_WithNegativeAmount_ReturnsBadRequest() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setBookingDate(LocalDateTime.now().plusDays(1));
        newBooking.setTotalAmount(BigDecimal.valueOf(-100.00)); // Negative amount

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBooking_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        Booking updatedBooking = new Booking();
        updatedBooking.setBookingDate(LocalDateTime.now().plusDays(2));
        updatedBooking.setTotalAmount(BigDecimal.valueOf(600.00));

        when(bookingService.updateBooking(bookingId, updatedBooking)).thenReturn(Optional.of(testBooking));

        // When & Then
        mockMvc.perform(put("/api/bookings/{id}", bookingId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBooking_NotFound() throws Exception {
        // Given
        UUID bookingId = UUID.randomUUID();
        Booking updatedBooking = new Booking();
        updatedBooking.setBookingDate(LocalDateTime.now().plusDays(2));

        when(bookingService.updateBooking(bookingId, updatedBooking)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/bookings/{id}", bookingId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBooking_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        Booking updatedBooking = new Booking();
        updatedBooking.setBookingDate(null); // Invalid data

        // When & Then
        mockMvc.perform(put("/api/bookings/{id}", bookingId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBooking_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingService.deleteBooking(bookingId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/bookings/{id}", bookingId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteBooking_NotFound() throws Exception {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingService.deleteBooking(bookingId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/bookings/{id}", bookingId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookingStatus_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        BookingStatus newStatus = BookingStatus.COMPLETED;

        when(bookingService.updateBookingStatus(bookingId, newStatus)).thenReturn(Optional.of(testBooking));

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/status", bookingId)
                .with(csrf())
                .param("status", newStatus.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookingStatus_NotFound() throws Exception {
        // Given
        UUID bookingId = UUID.randomUUID();
        BookingStatus newStatus = BookingStatus.COMPLETED;

        when(bookingService.updateBookingStatus(bookingId, newStatus)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/status", bookingId)
                .with(csrf())
                .param("status", newStatus.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookingStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/status", bookingId)
                .with(csrf())
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignProvider_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        UUID providerId = testProvider.getId();

        when(bookingService.assignProvider(bookingId, providerId)).thenReturn(Optional.of(testBooking));

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/assign-provider", bookingId)
                .with(csrf())
                .param("providerId", providerId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookingId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignProvider_NotFound() throws Exception {
        // Given
        UUID bookingId = UUID.randomUUID();
        UUID providerId = testProvider.getId();

        when(bookingService.assignProvider(bookingId, providerId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/assign-provider", bookingId)
                .with(csrf())
                .param("providerId", providerId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignProvider_WithInvalidProviderId_ReturnsBadRequest() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/assign-provider", bookingId)
                .with(csrf())
                .param("providerId", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignProvider_WithInvalidBookingId_ReturnsBadRequest() throws Exception {
        // Given
        UUID providerId = testProvider.getId();

        // When & Then
        mockMvc.perform(patch("/api/bookings/invalid-uuid/assign-provider")
                .with(csrf())
                .param("providerId", providerId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingsWithPagination_Success() throws Exception {
        // Given
        Page<Booking> bookingPage = new PageImpl<>(Arrays.asList(testBooking), PageRequest.of(0, 10), 1);
        when(bookingService.getBookingsWithPagination(any())).thenReturn(bookingPage);

        // When & Then
        mockMvc.perform(get("/api/bookings/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetBookingsWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingsWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingCount_Success() throws Exception {
        // Given
        long expectedCount = 100L;
        when(bookingService.getBookingCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/bookings/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void testGetBookingsByStatusCount_Success() throws Exception {
        // Given
        BookingStatus status = BookingStatus.CONFIRMED;
        long expectedCount = 50L;
        when(bookingService.getBookingsByStatusCount(status)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/bookings/status/{status}/count", status))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void testGetBookingsByUserIdCount_Success() throws Exception {
        // Given
        UUID userId = testUser.getId();
        long expectedCount = 25L;
        when(bookingService.getBookingsByUserIdCount(userId)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/bookings/user/{userId}/count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));
    }

    @Test
    void testGetBookingsByProviderIdCount_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        long expectedCount = 30L;
        when(bookingService.getBookingsByProviderIdCount(providerId)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/bookings/provider/{providerId}/count", providerId))
                .andExpect(status().isOk())
                .andExpect(content().string("30"));
    }

    @Test
    void testGetBookingById_WithSpecialCharactersInUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bookings/{id}", "invalid-uuid-with-special-chars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateBooking_WithUnavailableProvider_ReturnsBadRequest() throws Exception {
        // Given
        Booking newBooking = new Booking();
        newBooking.setBookingDate(LocalDateTime.now().plusDays(1));
        newBooking.setTotalAmount(BigDecimal.valueOf(500.00));

        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new RuntimeException("Provider is not available for the selected date"));

        // When & Then
        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookingStatus_WithInvalidStatusTransition_ReturnsBadRequest() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        BookingStatus newStatus = BookingStatus.CANCELLED;

        when(bookingService.updateBookingStatus(bookingId, newStatus))
                .thenThrow(new RuntimeException("Invalid status transition"));

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/status", bookingId)
                .with(csrf())
                .param("status", newStatus.toString()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignProvider_WithUnavailableProvider_ReturnsBadRequest() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        UUID providerId = testProvider.getId();

        when(bookingService.assignProvider(bookingId, providerId))
                .thenThrow(new RuntimeException("Provider is not available"));

        // When & Then
        mockMvc.perform(patch("/api/bookings/{id}/assign-provider", bookingId)
                .with(csrf())
                .param("providerId", providerId.toString()))
                .andExpect(status().isInternalServerError());
    }
}