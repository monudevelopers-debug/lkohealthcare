package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.repository.BookingRepository;
import com.lucknow.healthcare.repository.ProviderRepository;
import com.lucknow.healthcare.repository.ServiceRepository;
import com.lucknow.healthcare.repository.UserRepository;
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
 * Unit tests for BookingServiceImpl
 * 
 * Tests all business logic methods for booking management including
 * CRUD operations, status updates, provider assignment, and scheduling.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking testBooking;
    private User testUser;
    private Service testService;
    private Provider testProvider;

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
        testService.setDuration(60);

        testProvider = new Provider();
        testProvider.setId(UUID.randomUUID());
        testProvider.setName("Dr. Jane Smith");
        testProvider.setEmail("jane.smith@example.com");

        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setUser(testUser);
        testBooking.setService(testService);
        testBooking.setProvider(testProvider);
        testBooking.setScheduledDate(LocalDate.now().plusDays(1));
        testBooking.setScheduledTime(LocalTime.of(10, 0));
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setTotalAmount(new BigDecimal("500.00"));
        testBooking.setCreatedAt(LocalDateTime.now());
        testBooking.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateBooking_Success() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setUser(testUser);
        newBooking.setService(testService);
        newBooking.setScheduledDate(LocalDate.now().plusDays(2));
        newBooking.setScheduledTime(LocalTime.of(14, 0));
        newBooking.setTotalAmount(new BigDecimal("500.00"));

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.createBooking(newBooking);

        // Then
        assertNotNull(result);
        assertEquals(testBooking.getId(), result.getId());
        assertEquals(testBooking.getStatus(), result.getStatus());
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(serviceRepository, times(1)).findById(testService.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_WithInvalidUser_ThrowsException() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setUser(testUser);
        newBooking.setService(testService);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });
    }

    @Test
    void testCreateBooking_WithInvalidService_ThrowsException() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setUser(testUser);
        newBooking.setService(testService);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(serviceRepository.findById(testService.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });
    }

    @Test
    void testCreateBooking_WithPastDate_ThrowsException() {
        // Given
        Booking newBooking = new Booking();
        newBooking.setUser(testUser);
        newBooking.setService(testService);
        newBooking.setScheduledDate(LocalDate.now().minusDays(1));
        newBooking.setScheduledTime(LocalTime.of(10, 0));

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });
    }

    @Test
    void testGetBookingById_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When
        Optional<Booking> result = bookingService.getBookingById(bookingId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBooking.getId(), result.get().getId());
        assertEquals(testBooking.getStatus(), result.get().getStatus());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetBookingById_NotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.getBookingById(bookingId);

        // Then
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetAllBookings_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getAllBookings();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetBookingsByUser_Success() {
        // Given
        UUID userId = testUser.getId();
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByUserId(userId)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetBookingsByProvider_Success() {
        // Given
        UUID providerId = testProvider.getId();
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByProviderId(providerId)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByProvider(providerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByProviderId(providerId);
    }

    @Test
    void testGetBookingsByStatus_Success() {
        // Given
        BookingStatus status = BookingStatus.PENDING;
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByStatus(status)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByStatus(status);
    }

    @Test
    void testGetBookingsByDate_Success() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByScheduledDate(date)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByDate(date);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByScheduledDate(date);
    }

    @Test
    void testGetBookingsByDateRange_Success() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByScheduledDateBetween(startDate, endDate)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByScheduledDateBetween(startDate, endDate);
    }

    @Test
    void testUpdateBookingStatus_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        BookingStatus newStatus = BookingStatus.CONFIRMED;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Optional<Booking> result = bookingService.updateBookingStatus(bookingId, newStatus);

        // Then
        assertTrue(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testUpdateBookingStatus_NotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        BookingStatus newStatus = BookingStatus.CONFIRMED;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.updateBookingStatus(bookingId, newStatus);

        // Then
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testAssignProvider_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        UUID providerId = testProvider.getId();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(testProvider));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Optional<Booking> result = bookingService.assignProvider(bookingId, providerId);

        // Then
        assertTrue(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(providerRepository, times(1)).findById(providerId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testAssignProvider_BookingNotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        UUID providerId = testProvider.getId();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.assignProvider(bookingId, providerId);

        // Then
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(providerRepository, never()).findById(any(UUID.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testAssignProvider_ProviderNotFound() {
        // Given
        UUID bookingId = testBooking.getId();
        UUID providerId = UUID.randomUUID();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.assignProvider(bookingId, providerId);

        // Then
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(providerRepository, times(1)).findById(providerId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        String reason = "Customer requested cancellation";

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Optional<Booking> result = bookingService.cancelBooking(bookingId, reason);

        // Then
        assertTrue(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_NotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        String reason = "Customer requested cancellation";

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.cancelBooking(bookingId, reason);

        // Then
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testRescheduleBooking_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        LocalDate newDate = LocalDate.now().plusDays(3);
        LocalTime newTime = LocalTime.of(15, 0);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Optional<Booking> result = bookingService.rescheduleBooking(bookingId, newDate, newTime);

        // Then
        assertTrue(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testRescheduleBooking_NotFound() {
        // Given
        UUID bookingId = UUID.randomUUID();
        LocalDate newDate = LocalDate.now().plusDays(3);
        LocalTime newTime = LocalTime.of(15, 0);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.rescheduleBooking(bookingId, newDate, newTime);

        // Then
        assertFalse(result.isPresent());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testRescheduleBooking_WithPastDate_ThrowsException() {
        // Given
        UUID bookingId = testBooking.getId();
        LocalDate newDate = LocalDate.now().minusDays(1);
        LocalTime newTime = LocalTime.of(15, 0);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.rescheduleBooking(bookingId, newDate, newTime);
        });
    }

    @Test
    void testGetBookingsWithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = Arrays.asList(testBooking);
        Page<Booking> bookingPage = new PageImpl<>(bookings, pageable, 1);
        
        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);

        // When
        Page<Booking> result = bookingService.getBookingsWithPagination(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(bookingRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetBookingCount_Success() {
        // Given
        long expectedCount = 25L;
        when(bookingRepository.count()).thenReturn(expectedCount);

        // When
        long result = bookingService.getBookingCount();

        // Then
        assertEquals(expectedCount, result);
        verify(bookingRepository, times(1)).count();
    }

    @Test
    void testGetBookingsByStatusCount_Success() {
        // Given
        BookingStatus status = BookingStatus.PENDING;
        long expectedCount = 5L;
        when(bookingRepository.countByStatus(status)).thenReturn(expectedCount);

        // When
        long result = bookingService.getBookingsByStatusCount(status);

        // Then
        assertEquals(expectedCount, result);
        verify(bookingRepository, times(1)).countByStatus(status);
    }

    @Test
    void testGetBookingsByUserAndStatus_Success() {
        // Given
        UUID userId = testUser.getId();
        BookingStatus status = BookingStatus.PENDING;
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByUserIdAndStatus(userId, status)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByUserAndStatus(userId, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByUserIdAndStatus(userId, status);
    }

    @Test
    void testGetBookingsByProviderAndStatus_Success() {
        // Given
        UUID providerId = testProvider.getId();
        BookingStatus status = BookingStatus.CONFIRMED;
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByProviderIdAndStatus(providerId, status)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByProviderAndStatus(providerId, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByProviderIdAndStatus(providerId, status);
    }

    @Test
    void testGetUpcomingBookings_Success() {
        // Given
        LocalDate today = LocalDate.now();
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByScheduledDateAfter(today)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getUpcomingBookings();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByScheduledDateAfter(today);
    }

    @Test
    void testGetTodaysBookings_Success() {
        // Given
        LocalDate today = LocalDate.now();
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByScheduledDate(today)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getTodaysBookings();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByScheduledDate(today);
    }
}
