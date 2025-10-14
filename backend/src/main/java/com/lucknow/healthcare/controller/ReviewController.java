package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Review;
import com.lucknow.healthcare.service.interfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for Review entity operations
 * 
 * Provides REST endpoints for review management including
 * CRUD operations, rating calculations, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    /**
     * Create a new review
     * 
     * @param review the review to create
     * @return ResponseEntity containing the created review
     */
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        try {
            Review createdReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get review by ID
     * 
     * @param id the review ID
     * @return ResponseEntity containing the review if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable UUID id) {
        Optional<Review> reviewOpt = reviewService.findById(id);
        return reviewOpt.map(review -> ResponseEntity.ok(review))
                        .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get review by booking ID
     * 
     * @param bookingId the booking ID
     * @return ResponseEntity containing the review if found
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Review> getReviewByBookingId(@PathVariable UUID bookingId) {
        Optional<Review> reviewOpt = reviewService.findByBookingId(bookingId);
        return reviewOpt.map(review -> ResponseEntity.ok(review))
                        .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update review
     * 
     * @param id the review ID
     * @param review the updated review information
     * @return ResponseEntity containing the updated review
     */
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable UUID id, @RequestBody Review review) {
        try {
            review.setId(id);
            Review updatedReview = reviewService.updateReview(review);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all reviews with pagination
     * 
     * @param page the page number (default: 0)
     * @param size the page size (default: 20, max: 100)
     * @param sortBy the field to sort by (default: createdAt)
     * @param sortDir the sort direction (default: desc)
     * @return ResponseEntity containing page of reviews
     */
    @GetMapping
    public ResponseEntity<Page<Review>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            size = Math.min(size, 100);
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Review> reviews = reviewService.getAllReviews(pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get reviews by user with pagination
     * 
     * @param userId the user ID
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return ResponseEntity containing page of reviews
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Review>> getReviewsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Review> reviews = reviewService.getReviewsByUser(userId, pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get reviews by provider with pagination
     * 
     * @param providerId the provider ID
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return ResponseEntity containing page of reviews
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<Page<Review>> getReviewsByProvider(
            @PathVariable UUID providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Review> reviews = reviewService.getReviewsByProvider(providerId, pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get reviews by rating with pagination
     * 
     * @param rating the rating to filter by (1-5)
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return ResponseEntity containing page of reviews
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<Page<Review>> getReviewsByRating(
            @PathVariable Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().build();
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Review> reviews = reviewService.getReviewsByRating(rating, pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get reviews by rating range with pagination
     * 
     * @param minRating the minimum rating
     * @param maxRating the maximum rating
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return ResponseEntity containing page of reviews
     */
    @GetMapping("/rating-range")
    public ResponseEntity<Page<Review>> getReviewsByRatingRange(
            @RequestParam Integer minRating,
            @RequestParam Integer maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (minRating < 1 || maxRating > 5 || minRating > maxRating) {
                return ResponseEntity.badRequest().build();
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Review> reviews = reviewService.getReviewsByRatingRange(minRating, maxRating, pageable);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get reviews by date range
     * 
     * @param startDate the start date (format: yyyy-MM-ddTHH:mm:ss)
     * @param endDate the end date (format: yyyy-MM-ddTHH:mm:ss)
     * @return ResponseEntity containing list of reviews
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Review>> getReviewsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate);
            List<Review> reviews = reviewService.getReviewsByDateRange(startDateTime, endDateTime);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get top-rated providers
     * 
     * @param limit the maximum number of providers to return (default: 10)
     * @return ResponseEntity containing list of top-rated providers
     */
    @GetMapping("/top-rated-providers")
    public ResponseEntity<List<Review>> getTopRatedProviders(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Review> topRated = reviewService.getTopRatedProviders(limit);
            return ResponseEntity.ok(topRated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get recent reviews
     * 
     * @param limit the maximum number of reviews to return (default: 10)
     * @return ResponseEntity containing list of recent reviews
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Review>> getRecentReviews(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Review> recentReviews = reviewService.getRecentReviews(limit);
            return ResponseEntity.ok(recentReviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get review statistics
     * 
     * @return ResponseEntity containing review statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getReviewStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Count by rating
            stats.put("rating1Count", reviewService.countReviewsByRating(1));
            stats.put("rating2Count", reviewService.countReviewsByRating(2));
            stats.put("rating3Count", reviewService.countReviewsByRating(3));
            stats.put("rating4Count", reviewService.countReviewsByRating(4));
            stats.put("rating5Count", reviewService.countReviewsByRating(5));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get average rating by provider
     * 
     * @param providerId the provider ID
     * @return ResponseEntity containing the average rating
     */
    @GetMapping("/provider/{providerId}/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRatingByProvider(@PathVariable UUID providerId) {
        try {
            Double averageRating = reviewService.calculateAverageRatingByProvider(providerId);
            long totalReviews = reviewService.countReviewsByProvider(providerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("providerId", providerId);
            response.put("averageRating", averageRating);
            response.put("totalReviews", totalReviews);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get average rating by user
     * 
     * @param userId the user ID
     * @return ResponseEntity containing the average rating
     */
    @GetMapping("/user/{userId}/average-rating")
    public ResponseEntity<Map<String, Object>> getAverageRatingByUser(@PathVariable UUID userId) {
        try {
            Double averageRating = reviewService.calculateAverageRatingByUser(userId);
            long totalReviews = reviewService.countReviewsByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("averageRating", averageRating);
            response.put("totalReviews", totalReviews);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete review
     * 
     * @param id the review ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        try {
            boolean deleted = reviewService.deleteReview(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

