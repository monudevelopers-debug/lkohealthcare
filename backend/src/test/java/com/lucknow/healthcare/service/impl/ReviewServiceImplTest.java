package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Review;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.repository.ReviewRepository;
import com.lucknow.healthcare.repository.BookingRepository;
import com.lucknow.healthcare.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewServiceImpl
 * 
 * Tests all business logic methods for review management including
 * CRUD operations, rating calculations, provider reviews, and review validation.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;
    private Booking testBooking;
    private User testUser;
    private Provider testProvider;
    private Service testService;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");

        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Home Nursing");
        testService.setPrice(new BigDecimal("500.00"));

        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setName("Dr. Jane Smith");
        testProvider.setEmail("jane.smith@example.com");
        testProvider.setRating(4.5);
        testProvider.setReviewCount(10);

        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setUser(testUser);
        testBooking.setService(testService);
        testBooking.setProvider(testProvider);
        testBooking.setStatus(BookingStatus.COMPLETED);
        testBooking.setScheduledDate(LocalDate.now().minusDays(1));
        testBooking.setScheduledTime(LocalTime.of(10, 0));

        testReview = new Review();
        testReview.setId(UUID.randomUUID());
        testReview.setBooking(testBooking);
        testReview.setRating(5);
        testReview.setComment("Excellent service! Very professional and caring.");
        testReview.setIsPublic(true);
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateReview_Success() {
        // Given
        Review newReview = new Review();
        newReview.setBooking(testBooking);
        newReview.setRating(4);
        newReview.setComment("Good service overall");

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(providerRepository.findById(testProvider.getId())).thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any(Provider.class))).thenReturn(testProvider);

        // When
        Review result = reviewService.createReview(newReview);

        // Then
        assertNotNull(result);
        assertEquals(testReview.getId(), result.getId());
        assertEquals(testReview.getRating(), result.getRating());
        verify(bookingRepository, times(1)).findById(testBooking.getId());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(providerRepository, times(1)).findById(testProvider.getId());
        verify(providerRepository, times(1)).save(any(Provider.class));
    }

    @Test
    void testCreateReview_WithInvalidBooking_ThrowsException() {
        // Given
        Review newReview = new Review();
        newReview.setBooking(testBooking);

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(newReview);
        });
    }

    @Test
    void testCreateReview_WithInvalidRating_ThrowsException() {
        // Given
        Review newReview = new Review();
        newReview.setBooking(testBooking);
        newReview.setRating(6); // Invalid rating (should be 1-5)

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(newReview);
        });
    }

    @Test
    void testCreateReview_WithNegativeRating_ThrowsException() {
        // Given
        Review newReview = new Review();
        newReview.setBooking(testBooking);
        newReview.setRating(0); // Invalid rating (should be 1-5)

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(newReview);
        });
    }

    @Test
    void testCreateReview_WithIncompleteBooking_ThrowsException() {
        // Given
        testBooking.setStatus(BookingStatus.PENDING);
        Review newReview = new Review();
        newReview.setBooking(testBooking);
        newReview.setRating(5);

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(newReview);
        });
    }

    @Test
    void testGetReviewById_Success() {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // When
        Optional<Review> result = reviewService.getReviewById(reviewId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testReview.getId(), result.get().getId());
        assertEquals(testReview.getRating(), result.get().getRating());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetReviewById_NotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When
        Optional<Review> result = reviewService.getReviewById(reviewId);

        // Then
        assertFalse(result.isPresent());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    void testGetReviewsByBooking_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByBookingId(bookingId)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByBooking(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByBookingId(bookingId);
    }

    @Test
    void testGetReviewsByProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByBookingProviderId(providerId)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByProvider(providerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByBookingProviderId(providerId);
    }

    @Test
    void testGetReviewsByUser_Success() {
        // Given
        UUID userId = testUser.getId();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByBookingUserId(userId)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByBookingUserId(userId);
    }

    @Test
    void testGetReviewsByRating_Success() {
        // Given
        int rating = 5;
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByRating(rating)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByRating(rating);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByRating(rating);
    }

    @Test
    void testGetPublicReviews_Success() {
        // Given
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByIsPublicTrue()).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getPublicReviews();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsPublic());
        verify(reviewRepository, times(1)).findByIsPublicTrue();
    }

    @Test
    void testUpdateReview_Success() {
        // Given
        UUID reviewId = testReview.getId();
        Review updatedReview = new Review();
        updatedReview.setRating(4);
        updatedReview.setComment("Updated comment");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        Optional<Review> result = reviewService.updateReview(reviewId, updatedReview);

        // Then
        assertTrue(result.isPresent());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testUpdateReview_NotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        Review updatedReview = new Review();
        updatedReview.setRating(4);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When
        Optional<Review> result = reviewService.updateReview(reviewId, updatedReview);

        // Then
        assertFalse(result.isPresent());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testDeleteReview_Success() {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewRepository.existsById(reviewId)).thenReturn(true);

        // When
        boolean result = reviewService.deleteReview(reviewId);

        // Then
        assertTrue(result);
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    void testDeleteReview_NotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        // When
        boolean result = reviewService.deleteReview(reviewId);

        // Then
        assertFalse(result);
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void testToggleReviewVisibility_Success() {
        // Given
        UUID reviewId = testReview.getId();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        Optional<Review> result = reviewService.toggleReviewVisibility(reviewId);

        // Then
        assertTrue(result.isPresent());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testToggleReviewVisibility_NotFound() {
        // Given
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When
        Optional<Review> result = reviewService.toggleReviewVisibility(reviewId);

        // Then
        assertFalse(result.isPresent());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testGetReviewsWithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 1);
        
        when(reviewRepository.findAll(pageable)).thenReturn(reviewPage);

        // When
        Page<Review> result = reviewService.getReviewsWithPagination(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(reviewRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetReviewCount_Success() {
        // Given
        long expectedCount = 25L;
        when(reviewRepository.count()).thenReturn(expectedCount);

        // When
        long result = reviewService.getReviewCount();

        // Then
        assertEquals(expectedCount, result);
        verify(reviewRepository, times(1)).count();
    }

    @Test
    void testGetReviewsByRatingCount_Success() {
        // Given
        int rating = 5;
        long expectedCount = 15L;
        when(reviewRepository.countByRating(rating)).thenReturn(expectedCount);

        // When
        long result = reviewService.getReviewsByRatingCount(rating);

        // Then
        assertEquals(expectedCount, result);
        verify(reviewRepository, times(1)).countByRating(rating);
    }

    @Test
    void testGetAverageRatingByProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        double expectedAverage = 4.5;
        when(reviewRepository.findAverageRatingByBookingProviderId(providerId)).thenReturn(expectedAverage);

        // When
        double result = reviewService.getAverageRatingByProvider(providerId);

        // Then
        assertEquals(expectedAverage, result);
        verify(reviewRepository, times(1)).findAverageRatingByBookingProviderId(providerId);
    }

    @Test
    void testGetReviewCountByProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        long expectedCount = 10L;
        when(reviewRepository.countByBookingProviderId(providerId)).thenReturn(expectedCount);

        // When
        long result = reviewService.getReviewCountByProvider(providerId);

        // Then
        assertEquals(expectedCount, result);
        verify(reviewRepository, times(1)).countByBookingProviderId(providerId);
    }

    @Test
    void testGetReviewsByDateRange_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByCreatedAtBetween(startDate, endDate);
    }

    @Test
    void testGetTopRatedReviews_Success() {
        // Given
        int limit = 5;
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findTopRatedReviews(limit)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getTopRatedReviews(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findTopRatedReviews(limit);
    }

    @Test
    void testGetReviewsByProviderAndRating_Success() {
        // Given
        UUID providerId = testProvider.getId();
        int rating = 5;
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByBookingProviderIdAndRating(providerId, rating)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByProviderAndRating(providerId, rating);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByBookingProviderIdAndRating(providerId, rating);
    }

    @Test
    void testGetRecentReviews_Success() {
        // Given
        int limit = 10;
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findRecentReviews(limit)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getRecentReviews(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findRecentReviews(limit);
    }

    @Test
    void testSearchReviews_Success() {
        // Given
        String searchTerm = "excellent";
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByCommentContainingIgnoreCase(searchTerm)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.searchReviews(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByCommentContainingIgnoreCase(searchTerm);
    }

    @Test
    void testGetReviewsByUserAndProvider_Success() {
        // Given
        UUID userId = testUser.getId();
        UUID providerId = testProvider.getId();
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findByBookingUserIdAndBookingProviderId(userId, providerId)).thenReturn(reviews);

        // When
        List<Review> result = reviewService.getReviewsByUserAndProvider(userId, providerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByBookingUserIdAndBookingProviderId(userId, providerId);
    }
}
