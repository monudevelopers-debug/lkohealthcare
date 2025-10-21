package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Review;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.AvailabilityStatus;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.service.interfaces.BookingService;
import com.lucknow.healthcare.service.interfaces.ProviderService;
import com.lucknow.healthcare.service.interfaces.ReviewService;
import com.lucknow.healthcare.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Provider entity operations
 * 
 * Provides REST endpoints for provider management including
 * CRUD operations, availability management, rating updates, and search functionality.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/providers")
@CrossOrigin(origins = "*")
public class ProviderController {
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ReviewService reviewService;
    
    /**
     * Create a new provider
     * 
     * @param provider the provider to create
     * @return ResponseEntity containing the created provider
     */
    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        try {
            Provider createdProvider = providerService.createProvider(provider);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get provider by ID
     * 
     * @param id the provider ID
     * @return ResponseEntity containing the provider if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable UUID id) {
        Optional<Provider> providerOpt = providerService.findById(id);
        return providerOpt.map(provider -> ResponseEntity.ok(provider))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get provider by email
     * 
     * @param email the provider's email
     * @return ResponseEntity containing the provider if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Provider> getProviderByEmail(@PathVariable String email) {
        Optional<Provider> providerOpt = providerService.findByEmail(email);
        return providerOpt.map(provider -> ResponseEntity.ok(provider))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update provider
     * 
     * @param id the provider ID
     * @param provider the updated provider information
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(@PathVariable UUID id, @RequestBody Provider provider) {
        try {
            provider.setId(id);
            Provider updatedProvider = providerService.updateProvider(provider);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update provider availability status
     * 
     * @param id the provider ID
     * @param availabilityStatus the new availability status
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<Provider> updateProviderAvailability(@PathVariable UUID id, @RequestParam AvailabilityStatus availabilityStatus) {
        try {
            Provider updatedProvider = providerService.updateProviderAvailability(id, availabilityStatus);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update provider verification status
     * 
     * @param id the provider ID
     * @param isVerified the new verification status
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}/verification")
    public ResponseEntity<Provider> updateProviderVerification(@PathVariable UUID id, @RequestParam Boolean isVerified) {
        try {
            Provider updatedProvider = providerService.updateProviderVerification(id, isVerified);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update provider rating
     * 
     * @param id the provider ID
     * @param rating the new rating (1-5)
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/{id}/rating")
    public ResponseEntity<Provider> updateProviderRating(@PathVariable UUID id, @RequestParam Integer rating) {
        try {
            Provider updatedProvider = providerService.updateProviderRating(id, rating);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all providers
     * 
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by availability status
     * 
     * @param availabilityStatus the availability status
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/availability/{availabilityStatus}")
    public ResponseEntity<List<Provider>> getProvidersByAvailability(@PathVariable AvailabilityStatus availabilityStatus) {
        List<Provider> providers = providerService.getProvidersByAvailability(availabilityStatus);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by verification status
     * 
     * @param isVerified the verification status
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/verification/{isVerified}")
    public ResponseEntity<List<Provider>> getProvidersByVerification(@PathVariable Boolean isVerified) {
        List<Provider> providers = providerService.getProvidersByVerification(isVerified);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get verified providers
     * 
     * @return ResponseEntity containing the list of verified providers
     */
    @GetMapping("/verified")
    public ResponseEntity<List<Provider>> getVerifiedProviders() {
        List<Provider> providers = providerService.getVerifiedProviders();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available and verified providers
     * 
     * @return ResponseEntity containing the list of available and verified providers
     */
    @GetMapping("/available-verified")
    public ResponseEntity<List<Provider>> getAvailableAndVerifiedProviders() {
        List<Provider> providers = providerService.getAvailableAndVerifiedProviders();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by minimum rating
     * 
     * @param minRating the minimum rating
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<Provider>> getProvidersByMinRating(@PathVariable Double minRating) {
        List<Provider> providers = providerService.getProvidersByMinRating(minRating);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available providers by minimum rating
     * 
     * @param minRating the minimum rating
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/available/rating/{minRating}")
    public ResponseEntity<List<Provider>> getAvailableProvidersByMinRating(@PathVariable Double minRating) {
        List<Provider> providers = providerService.getAvailableProvidersByMinRating(minRating);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/experience")
    public ResponseEntity<List<Provider>> getProvidersByExperienceRange(@RequestParam Integer minExperience, @RequestParam Integer maxExperience) {
        List<Provider> providers = providerService.getProvidersByExperienceRange(minExperience, maxExperience);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available providers by experience range
     * 
     * @param minExperience the minimum experience in years
     * @param maxExperience the maximum experience in years
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/available/experience")
    public ResponseEntity<List<Provider>> getAvailableProvidersByExperienceRange(@RequestParam Integer minExperience, @RequestParam Integer maxExperience) {
        List<Provider> providers = providerService.getAvailableProvidersByExperienceRange(minExperience, maxExperience);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Search providers by name
     * 
     * @param name the name to search for
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/search")
    public ResponseEntity<List<Provider>> searchProvidersByName(@RequestParam String name) {
        List<Provider> providers = providerService.searchProvidersByName(name);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Search providers by qualification
     * 
     * @param qualification the qualification to search for
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/search/qualification")
    public ResponseEntity<List<Provider>> searchProvidersByQualification(@RequestParam String qualification) {
        List<Provider> providers = providerService.searchProvidersByQualification(qualification);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get top-rated providers
     * 
     * @param limit the maximum number of providers to return
     * @return ResponseEntity containing the list of top-rated providers
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Provider>> getTopRatedProviders(@RequestParam(defaultValue = "10") int limit) {
        List<Provider> providers = providerService.getTopRatedProviders(limit);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available providers for a specific date and time
     * 
     * @param date the date to check availability
     * @param time the time to check availability
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/available")
    public ResponseEntity<List<Provider>> getAvailableProvidersForDateTime(@RequestParam LocalDate date, @RequestParam LocalTime time) {
        List<Provider> providers = providerService.getAvailableProvidersForDateTime(date, time);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get all providers with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of providers
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Provider>> getAllProviders(Pageable pageable) {
        Page<Provider> providers = providerService.getAllProviders(pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by availability status with pagination
     * 
     * @param availabilityStatus the availability status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of providers
     */
    @GetMapping("/availability/{availabilityStatus}/page")
    public ResponseEntity<Page<Provider>> getProvidersByAvailability(@PathVariable AvailabilityStatus availabilityStatus, Pageable pageable) {
        Page<Provider> providers = providerService.getProvidersByAvailability(availabilityStatus, pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get providers by verification status with pagination
     * 
     * @param isVerified the verification status
     * @param pageable pagination information
     * @return ResponseEntity containing the page of providers
     */
    @GetMapping("/verification/{isVerified}/page")
    public ResponseEntity<Page<Provider>> getProvidersByVerification(@PathVariable Boolean isVerified, Pageable pageable) {
        Page<Provider> providers = providerService.getProvidersByVerification(isVerified, pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get available and verified providers with pagination
     * 
     * @param pageable pagination information
     * @return ResponseEntity containing the page of available and verified providers
     */
    @GetMapping("/available-verified/page")
    public ResponseEntity<Page<Provider>> getAvailableAndVerifiedProviders(Pageable pageable) {
        Page<Provider> providers = providerService.getAvailableAndVerifiedProviders(pageable);
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Count providers by availability status
     * 
     * @param availabilityStatus the availability status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/availability/{availabilityStatus}")
    public ResponseEntity<Long> countProvidersByAvailability(@PathVariable AvailabilityStatus availabilityStatus) {
        long count = providerService.countProvidersByAvailability(availabilityStatus);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count providers by verification status
     * 
     * @param isVerified the verification status
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/verification/{isVerified}")
    public ResponseEntity<Long> countProvidersByVerification(@PathVariable Boolean isVerified) {
        long count = providerService.countProvidersByVerification(isVerified);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Count available and verified providers
     * 
     * @return ResponseEntity containing the count
     */
    @GetMapping("/count/available-verified")
    public ResponseEntity<Long> countAvailableAndVerifiedProviders() {
        long count = providerService.countAvailableAndVerifiedProviders();
        return ResponseEntity.ok(count);
    }
    
    /**
     * Delete provider
     * 
     * @param id the provider ID
     * @return ResponseEntity indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID id) {
        try {
            boolean success = providerService.deleteProvider(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ============ Provider Dashboard Specific Endpoints ============
    
    /**
     * Get current provider's profile
     * 
     * @return ResponseEntity containing the provider profile
     */
    @GetMapping("/profile")
    public ResponseEntity<Provider> getProviderProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            return ResponseEntity.ok(providerOpt.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update current provider's profile
     * 
     * @param provider the updated provider information
     * @return ResponseEntity containing the updated provider
     */
    @PutMapping("/profile")
    public ResponseEntity<Provider> updateProviderProfile(@RequestBody Provider provider) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider existingProvider = providerOpt.get();
            provider.setId(existingProvider.getId());
            Provider updatedProvider = providerService.updateProvider(provider);
            return ResponseEntity.ok(updatedProvider);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update current provider's availability
     * 
     * @param request containing isAvailable boolean
     * @return ResponseEntity containing the updated provider
     */
    @PatchMapping("/availability")
    public ResponseEntity<Provider> updateAvailability(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            Boolean isAvailable = (Boolean) request.get("isAvailable");
            AvailabilityStatus status = isAvailable ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.OFF_DUTY;
            
            Provider updatedProvider = providerService.updateProviderAvailability(provider.getId(), status);
            return ResponseEntity.ok(updatedProvider);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current provider's bookings
     * 
     * @param page page number
     * @param size page size
     * @param status optional booking status filter
     * @return ResponseEntity containing paginated bookings
     */
    @GetMapping("/bookings")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<Page<Booking>> getProviderBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            Pageable pageable = PageRequest.of(page, size);
            Page<Booking> bookings = bookingService.getBookingsByProvider(provider, pageable);
            
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get current provider's statistics
     * 
     * @param period time period (today, week, month)
     * @return ResponseEntity containing provider statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProviderStats(
            @RequestParam(defaultValue = "month") String period) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            Map<String, Object> stats = new HashMap<>();
            
            try {
                // Get all provider bookings
                List<Booking> allBookings = bookingService.getBookingsByProvider(provider);
                LocalDate today = LocalDate.now();
                
                // Calculate today's bookings
                long todayBookings = allBookings.stream()
                    .filter(b -> b.getScheduledDate() != null && b.getScheduledDate().equals(today))
                    .count();
                
                // Calculate active bookings (PENDING, CONFIRMED, IN_PROGRESS)
                long activeBookings = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.PENDING || 
                                b.getStatus() == BookingStatus.CONFIRMED ||
                                b.getStatus() == BookingStatus.IN_PROGRESS)
                    .count();
                
                // Calculate total earnings (from COMPLETED bookings)
                double totalEarnings = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                    .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0.0)
                    .sum();
                
                // Calculate yesterday's bookings for change percentage
                LocalDate yesterday = today.minusDays(1);
                long yesterdayBookings = allBookings.stream()
                    .filter(b -> b.getScheduledDate() != null && b.getScheduledDate().equals(yesterday))
                    .count();
                
                double todayBookingsChange = yesterdayBookings > 0 
                    ? ((double)(todayBookings - yesterdayBookings) / yesterdayBookings) * 100 
                    : 0;
                
                stats.put("todayBookings", todayBookings);
                stats.put("activeBookings", activeBookings);
                stats.put("totalEarnings", totalEarnings);
                stats.put("rating", provider.getRating() != null ? provider.getRating() : 0.0);
                stats.put("todayBookingsChange", todayBookingsChange);
                stats.put("activeBookingsChange", 0.0);
                stats.put("earningsChange", 0.0);
                stats.put("ratingChange", 0.0);
                
                return ResponseEntity.ok(stats);
            } catch (Exception calcEx) {
                System.err.println("Error calculating stats: " + calcEx.getMessage());
                calcEx.printStackTrace();
                
                // Return default stats on error
                stats.put("todayBookings", 0);
                stats.put("activeBookings", 0);
                stats.put("totalEarnings", 0.0);
                stats.put("rating", provider.getRating() != null ? provider.getRating() : 0.0);
                stats.put("todayBookingsChange", 0.0);
                stats.put("activeBookingsChange", 0.0);
                stats.put("earningsChange", 0.0);
                stats.put("ratingChange", 0.0);
                
                return ResponseEntity.ok(stats);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get current provider's reviews
     * 
     * @param page page number
     * @param size page size
     * @return ResponseEntity containing paginated reviews
     */
    @GetMapping("/reviews")
    public ResponseEntity<Map<String, Object>> getProviderReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviews = reviewService.getReviewsByProvider(provider.getId(), pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", reviews.getContent());
            response.put("totalElements", reviews.getTotalElements());
            response.put("totalPages", reviews.getTotalPages());
            response.put("number", reviews.getNumber());
            response.put("size", reviews.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get current provider's schedule for a specific date
     * 
     * @param date the date to get schedule for (format: yyyy-MM-dd)
     * @return ResponseEntity containing bookings for the date
     */
    @GetMapping("/schedule")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<Booking>> getProviderSchedule(
            @RequestParam String date) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            LocalDate scheduleDate = LocalDate.parse(date);
            // Get all bookings for the provider and filter by date
            List<Booking> allBookings = bookingService.getBookingsByProvider(provider);
            List<Booking> bookings = allBookings.stream()
                .filter(b -> b.getScheduledDate() != null && b.getScheduledDate().equals(scheduleDate))
                .toList();
            
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get recent bookings for current provider
     * 
     * @param limit number of recent bookings to return
     * @return ResponseEntity containing recent bookings
     */
    @GetMapping("/recent-bookings")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<Booking>> getRecentBookings(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            List<Booking> allBookings = bookingService.getBookingsByProvider(provider);
            
            // Sort by created date descending and limit
            List<Booking> recentBookings = allBookings.stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(limit)
                .toList();
            
            return ResponseEntity.ok(recentBookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get earnings data for current provider
     * 
     * @param period time period (today, week, month, year)
     * @return ResponseEntity containing earnings data
     */
    @GetMapping("/earnings")
    public ResponseEntity<Map<String, Object>> getEarnings(
            @RequestParam(defaultValue = "month") String period) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            List<Booking> bookings = bookingService.getBookingsByProvider(provider);
            
            // Filter completed bookings for the period
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = switch (period.toLowerCase()) {
                case "today" -> now.toLocalDate().atStartOfDay();
                case "week" -> now.minusWeeks(1);
                case "year" -> now.minusYears(1);
                default -> now.minusMonths(1); // month
            };
            
            List<Booking> periodBookings = bookings.stream()
                .filter(b -> b.getStatus() == com.lucknow.healthcare.enums.BookingStatus.COMPLETED)
                .filter(b -> b.getUpdatedAt().isAfter(startDate))
                .toList();
            
            double total = periodBookings.stream()
                .mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0.0)
                .sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", total);
            response.put("breakdown", periodBookings);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get payment history for current provider
     * 
     * @param page page number
     * @param size page size
     * @return ResponseEntity containing paginated payment history
     */
    @GetMapping("/payments")
    public ResponseEntity<Map<String, Object>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            Optional<Provider> providerOpt = providerService.findByEmail(email);
            if (providerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Provider provider = providerOpt.get();
            
            // Get all completed bookings for the provider (simulating payments)
            List<Booking> allBookings = bookingService.getBookingsByProvider(provider).stream()
                .filter(b -> b.getStatus() == com.lucknow.healthcare.enums.BookingStatus.COMPLETED)
                .sorted((b1, b2) -> b2.getUpdatedAt().compareTo(b1.getUpdatedAt()))
                .toList();
            
            int start = page * size;
            int end = Math.min(start + size, allBookings.size());
            List<Booking> pageBookings = start < allBookings.size() ? 
                allBookings.subList(start, end) : List.of();
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", pageBookings);
            response.put("totalElements", allBookings.size());
            response.put("totalPages", (int) Math.ceil((double) allBookings.size() / size));
            response.put("number", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Add service to provider
     * 
     * @param id the provider ID
     * @param serviceId the service ID to add
     * @return ResponseEntity containing the updated provider
     */
    @PostMapping("/{id}/services/{serviceId}")
    public ResponseEntity<Provider> addServiceToProvider(@PathVariable UUID id, @PathVariable UUID serviceId) {
        try {
            Provider updatedProvider = providerService.addServiceToProvider(id, serviceId);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Remove service from provider
     * 
     * @param id the provider ID
     * @param serviceId the service ID to remove
     * @return ResponseEntity containing the updated provider
     */
    @DeleteMapping("/{id}/services/{serviceId}")
    public ResponseEntity<Provider> removeServiceFromProvider(@PathVariable UUID id, @PathVariable UUID serviceId) {
        try {
            Provider updatedProvider = providerService.removeServiceFromProvider(id, serviceId);
            return ResponseEntity.ok(updatedProvider);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get services offered by a provider
     * 
     * @param id the provider ID
     * @return ResponseEntity containing the list of services
     */
    @GetMapping("/{id}/services")
    public ResponseEntity<List<com.lucknow.healthcare.entity.Service>> getProviderServices(@PathVariable UUID id) {
        try {
            // Use a native query to fetch provider services with JOIN to avoid lazy loading
            List<com.lucknow.healthcare.entity.Service> services = providerService.getProviderServicesEager(id);
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get providers who offer a specific service
     * 
     * @param serviceId the service ID
     * @return ResponseEntity containing the list of providers
     */
    @GetMapping("/by-service/{serviceId}")
    public ResponseEntity<List<Provider>> getProvidersByService(@PathVariable UUID serviceId) {
        try {
            List<Provider> providers = providerService.getProvidersByService(serviceId);
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get available providers who offer a specific service
     * 
     * @param serviceId the service ID
     * @return ResponseEntity containing the list of available providers
     */
    @GetMapping("/by-service/{serviceId}/available")
    public ResponseEntity<List<Provider>> getAvailableProvidersByService(@PathVariable UUID serviceId) {
        try {
            List<Provider> providers = providerService.getAvailableProvidersByService(serviceId);
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
