package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Review;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.ProviderStatus;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.ReviewService;
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
 * Unit tests for ReviewController
 * 
 * Tests all review management endpoints including CRUD operations,
 * rating calculations, review moderation, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review testReview;
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
        testBooking.setStatus(BookingStatus.COMPLETED);
        testBooking.setTotalAmount(BigDecimal.valueOf(500.00));
        testBooking.setCreatedAt(LocalDateTime.now());
        testBooking.setUpdatedAt(LocalDateTime.now());

        // Setup test review
        testReview = new Review();
        testReview.setId(UUID.randomUUID());
        testReview.setBooking(testBooking);
        testReview.setRating(5);
        testReview.setComment("Excellent service! Very professional and helpful.");
        testReview.setIsVerified(true);
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllReviews_Success() throws Exception {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getAllReviews()).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testReview.getId().toString()))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Excellent service! Very professional and helpful."))
                .andExpect(jsonPath("$[0].isVerified").value(true));
    }

    @Test
    void testGetAllReviews_EmptyList() throws Exception {
        // Given
        when(reviewService.getAllReviews()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetReviewById_Success() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.of(testReview));

        // When & Then
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId.toString()))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent service! Very professional and helpful."))
                .andExpect(jsonPath("$.isVerified").value(true));
    }

    @Test
    void testGetReviewById_NotFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetReviewById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewsByBookingId_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewsByBookingId(bookingId)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/booking/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testReview.getId().toString()));
    }

    @Test
    void testGetReviewsByBookingId_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/booking/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewsByProviderId_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewsByProviderId(providerId)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/provider/{providerId}", providerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testReview.getId().toString()));
    }

    @Test
    void testGetReviewsByProviderId_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/provider/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewsByRating_Success() throws Exception {
        // Given
        int rating = 5;
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.getReviewsByRating(rating)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/rating/{rating}", rating))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void testGetReviewsByRating_WithInvalidRating_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/rating/6"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewsByRating_WithNegativeRating_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/rating/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetVerifiedReviews_Success() throws Exception {
        // Given
        List<Review> verifiedReviews = Arrays.asList(testReview);
        when(reviewService.getVerifiedReviews()).thenReturn(verifiedReviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/verified"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isVerified").value(true));
    }

    @Test
    void testGetUnverifiedReviews_Success() throws Exception {
        // Given
        testReview.setIsVerified(false);
        List<Review> unverifiedReviews = Arrays.asList(testReview);
        when(reviewService.getUnverifiedReviews()).thenReturn(unverifiedReviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/unverified"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isVerified").value(false));
    }

    @Test
    void testSearchReviews_Success() throws Exception {
        // Given
        String searchTerm = "excellent";
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewService.searchReviews(searchTerm)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].comment").value("Excellent service! Very professional and helpful."));
    }

    @Test
    void testSearchReviews_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchReviews_WithNullSearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateReview_Success() throws Exception {
        // Given
        Review newReview = new Review();
        newReview.setRating(4);
        newReview.setComment("Good service, would recommend.");

        when(reviewService.createReview(any(Review.class))).thenReturn(testReview);

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testReview.getId().toString()))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void testCreateReview_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        Review newReview = new Review();
        newReview.setRating(4);
        newReview.setComment("Good service, would recommend.");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateReview_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        Review newReview = new Review();
        newReview.setRating(6); // Invalid rating
        newReview.setComment("Good service, would recommend.");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateReview_WithNegativeRating_ReturnsBadRequest() throws Exception {
        // Given
        Review newReview = new Review();
        newReview.setRating(-1); // Invalid rating
        newReview.setComment("Good service, would recommend.");

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateReview_WithEmptyComment_ReturnsBadRequest() throws Exception {
        // Given
        Review newReview = new Review();
        newReview.setRating(4);
        newReview.setComment(""); // Empty comment

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testUpdateReview_Success() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        Review updatedReview = new Review();
        updatedReview.setRating(4);
        updatedReview.setComment("Updated review comment");

        when(reviewService.updateReview(reviewId, updatedReview)).thenReturn(Optional.of(testReview));

        // When & Then
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReview)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId.toString()));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testUpdateReview_NotFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        Review updatedReview = new Review();
        updatedReview.setRating(4);
        updatedReview.setComment("Updated review comment");

        when(reviewService.updateReview(reviewId, updatedReview)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReview)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testUpdateReview_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        Review updatedReview = new Review();
        updatedReview.setRating(6); // Invalid rating
        updatedReview.setComment("Updated review comment");

        // When & Then
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReview)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testDeleteReview_Success() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewService.deleteReview(reviewId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testDeleteReview_NotFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewService.deleteReview(reviewId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleReviewVerification_Success() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewService.toggleReviewVerification(reviewId)).thenReturn(Optional.of(testReview));

        // When & Then
        mockMvc.perform(patch("/api/reviews/{id}/toggle-verification", reviewId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testToggleReviewVerification_NotFound() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewService.toggleReviewVerification(reviewId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(patch("/api/reviews/{id}/toggle-verification", reviewId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testToggleReviewVerification_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // Given
        UUID reviewId = testReview.getId();

        // When & Then
        mockMvc.perform(patch("/api/reviews/{id}/toggle-verification", reviewId)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetReviewsWithPagination_Success() throws Exception {
        // Given
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(testReview), PageRequest.of(0, 10), 1);
        when(reviewService.getReviewsWithPagination(any())).thenReturn(reviewPage);

        // When & Then
        mockMvc.perform(get("/api/reviews/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testGetReviewsWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewsWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewCount_Success() throws Exception {
        // Given
        long expectedCount = 50L;
        when(reviewService.getReviewCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/reviews/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    void testGetReviewsByRatingCount_Success() throws Exception {
        // Given
        int rating = 5;
        long expectedCount = 30L;
        when(reviewService.getReviewsByRatingCount(rating)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/reviews/rating/{rating}/count", rating))
                .andExpect(status().isOk())
                .andExpect(content().string("30"));
    }

    @Test
    void testGetVerifiedReviewCount_Success() throws Exception {
        // Given
        long expectedCount = 40L;
        when(reviewService.getVerifiedReviewCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/reviews/verified/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("40"));
    }

    @Test
    void testGetUnverifiedReviewCount_Success() throws Exception {
        // Given
        long expectedCount = 10L;
        when(reviewService.getUnverifiedReviewCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/reviews/unverified/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void testGetAverageRatingByProvider_Success() throws Exception {
        // Given
        UUID providerId = testProvider.getId();
        double expectedRating = 4.5;
        when(reviewService.getAverageRatingByProvider(providerId)).thenReturn(expectedRating);

        // When & Then
        mockMvc.perform(get("/api/reviews/provider/{providerId}/average-rating", providerId))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void testGetAverageRatingByProvider_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/provider/invalid-uuid/average-rating"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetReviewById_WithSpecialCharactersInUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/reviews/{id}", "invalid-uuid-with-special-chars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testCreateReview_WithDuplicateBooking_ReturnsConflict() throws Exception {
        // Given
        Review newReview = new Review();
        newReview.setRating(4);
        newReview.setComment("Good service, would recommend.");

        when(reviewService.createReview(any(Review.class)))
                .thenThrow(new RuntimeException("Review for this booking already exists"));

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testUpdateReview_WithInvalidBookingStatus_ReturnsBadRequest() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        Review updatedReview = new Review();
        updatedReview.setRating(4);
        updatedReview.setComment("Updated review comment");

        when(reviewService.updateReview(reviewId, updatedReview))
                .thenThrow(new RuntimeException("Review cannot be updated for this booking status"));

        // When & Then
        mockMvc.perform(put("/api/reviews/{id}", reviewId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReview)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER", username = "john.doe@example.com")
    void testDeleteReview_WithInvalidBookingStatus_ReturnsBadRequest() throws Exception {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewService.deleteReview(reviewId))
                .thenThrow(new RuntimeException("Review cannot be deleted for this booking status"));

        // When & Then
        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
