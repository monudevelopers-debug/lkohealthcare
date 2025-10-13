package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Review;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.repository.ReviewRepository;
import com.lucknow.healthcare.service.interfaces.ReviewService;
import com.lucknow.healthcare.service.interfaces.BookingService;
import com.lucknow.healthcare.service.interfaces.UserService;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Review entity operations (Phase 1.5)
 * 
 * Implements business logic for review management including
 * CRUD operations, rating calculations, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProviderService providerService;
    
    @Override
    public Review createReview(Review review) {
        // Validate booking exists
        if (review.getBooking() == null || review.getBooking().getId() == null) {
            throw new IllegalArgumentException("Booking is required for review");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(review.getBooking().getId());
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + review.getBooking().getId());
        }
        
        // Validate user exists
        if (review.getUser() == null || review.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required for review");
        }
        
        Optional<User> userOpt = userService.findById(review.getUser().getId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + review.getUser().getId());
        }
        
        // Validate provider exists
        if (review.getProvider() == null || review.getProvider().getId() == null) {
            throw new IllegalArgumentException("Provider is required for review");
        }
        
        Optional<Provider> providerOpt = providerService.findById(review.getProvider().getId());
        if (providerOpt.isEmpty()) {
            throw new IllegalArgumentException("Provider not found with ID: " + review.getProvider().getId());
        }
        
        // Check if review already exists for this booking
        if (reviewRepository.findByBookingId(review.getBooking().getId()).isPresent()) {
            throw new IllegalArgumentException("Review already exists for booking: " + review.getBooking().getId());
        }
        
        // Validate rating
        if (!review.isValidRating()) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        return reviewRepository.save(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findById(UUID id) {
        return reviewRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findByBookingId(UUID bookingId) {
        return reviewRepository.findByBookingId(bookingId);
    }
    
    @Override
    public Review updateReview(Review review) {
        if (!reviewRepository.existsById(review.getId())) {
            throw new IllegalArgumentException("Review not found with ID: " + review.getId());
        }
        
        // Validate rating if updated
        if (review.getRating() != null && !review.isValidRating()) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        return reviewRepository.save(review);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByUser(UUID userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByProvider(UUID providerId) {
        return reviewRepository.findByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating) {
        return reviewRepository.findByRatingBetween(minRating, maxRating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return reviewRepository.findByCreatedAtBetween(startDateTime, endDateTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByUser(UUID userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByProvider(UUID providerId, Pageable pageable) {
        return reviewRepository.findByProviderId(providerId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByRating(Integer rating, Pageable pageable) {
        return reviewRepository.findByRating(rating, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating, Pageable pageable) {
        return reviewRepository.findByRatingBetween(minRating, maxRating, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReviewsByUser(UUID userId) {
        return reviewRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReviewsByProvider(UUID providerId) {
        return reviewRepository.countByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReviewsByRating(Integer rating) {
        return reviewRepository.countByRating(rating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageRatingByProvider(UUID providerId) {
        return reviewRepository.calculateAverageRatingByProvider(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageRatingByUser(UUID userId) {
        return reviewRepository.calculateAverageRatingByUser(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getTopRatedProviders(int limit) {
        return reviewRepository.findTopRatedProviders(limit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Review> getRecentReviews(int limit) {
        return reviewRepository.findRecentReviews(limit);
    }
    
    @Override
    public boolean deleteReview(UUID id) {
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        
        if (reviewOpt.isEmpty()) {
            throw new IllegalArgumentException("Review not found with ID: " + id);
        }
        
        reviewRepository.delete(reviewOpt.get());
        return true;
    }
}
