package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Payment;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentMethod;
import com.lucknow.healthcare.enums.PaymentStatus;
import com.lucknow.healthcare.repository.PaymentRepository;
import com.lucknow.healthcare.repository.BookingRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentServiceImpl
 * 
 * Tests all business logic methods for payment management including
 * payment processing, refunds, status updates, and transaction tracking.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private Booking testBooking;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");

        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setUser(testUser);
        testBooking.setStatus(BookingStatus.CONFIRMED);
        testBooking.setTotalAmount(new BigDecimal("500.00"));

        testPayment = new Payment();
        testPayment.setId(UUID.randomUUID());
        testPayment.setBooking(testBooking);
        testPayment.setAmount(new BigDecimal("500.00"));
        testPayment.setPaymentMethod(PaymentMethod.CARD);
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setGatewayTransactionId("txn_123456789");
        testPayment.setRefundedAmount(BigDecimal.ZERO);
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreatePayment_Success() {
        // Given
        Payment newPayment = new Payment();
        newPayment.setBooking(testBooking);
        newPayment.setAmount(new BigDecimal("500.00"));
        newPayment.setPaymentMethod(PaymentMethod.CARD);

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        Payment result = paymentService.createPayment(newPayment);

        // Then
        assertNotNull(result);
        assertEquals(testPayment.getId(), result.getId());
        assertEquals(testPayment.getAmount(), result.getAmount());
        verify(bookingRepository, times(1)).findById(testBooking.getId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testCreatePayment_WithInvalidBooking_ThrowsException() {
        // Given
        Payment newPayment = new Payment();
        newPayment.setBooking(testBooking);

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(newPayment);
        });
    }

    @Test
    void testCreatePayment_WithNullAmount_ThrowsException() {
        // Given
        Payment newPayment = new Payment();
        newPayment.setBooking(testBooking);
        newPayment.setAmount(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(newPayment);
        });
    }

    @Test
    void testCreatePayment_WithNegativeAmount_ThrowsException() {
        // Given
        Payment newPayment = new Payment();
        newPayment.setBooking(testBooking);
        newPayment.setAmount(new BigDecimal("-100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(newPayment);
        });
    }

    @Test
    void testGetPaymentById_Success() {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // When
        Optional<Payment> result = paymentService.getPaymentById(paymentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPayment.getId(), result.get().getId());
        assertEquals(testPayment.getAmount(), result.get().getAmount());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Given
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When
        Optional<Payment> result = paymentService.getPaymentById(paymentId);

        // Then
        assertFalse(result.isPresent());
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentsByBooking_Success() {
        // Given
        UUID bookingId = testBooking.getId();
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByBookingId(bookingId)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByBooking(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByBookingId(bookingId);
    }

    @Test
    void testGetPaymentsByUser_Success() {
        // Given
        UUID userId = testUser.getId();
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByBookingUserId(userId)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByBookingUserId(userId);
    }

    @Test
    void testGetPaymentsByStatus_Success() {
        // Given
        PaymentStatus status = PaymentStatus.COMPLETED;
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByStatus(status)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByStatus(status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByStatus(status);
    }

    @Test
    void testGetPaymentsByMethod_Success() {
        // Given
        PaymentMethod method = PaymentMethod.CARD;
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByPaymentMethod(method)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByMethod(method);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByPaymentMethod(method);
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        // Given
        UUID paymentId = testPayment.getId();
        PaymentStatus newStatus = PaymentStatus.FAILED;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        Optional<Payment> result = paymentService.updatePaymentStatus(paymentId, newStatus);

        // Then
        assertTrue(result.isPresent());
        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testUpdatePaymentStatus_NotFound() {
        // Given
        UUID paymentId = UUID.randomUUID();
        PaymentStatus newStatus = PaymentStatus.FAILED;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When
        Optional<Payment> result = paymentService.updatePaymentStatus(paymentId, newStatus);

        // Then
        assertFalse(result.isPresent());
        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testProcessRefund_Success() {
        // Given
        UUID paymentId = testPayment.getId();
        BigDecimal refundAmount = new BigDecimal("250.00");
        String reason = "Partial refund requested";

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        Optional<Payment> result = paymentService.processRefund(paymentId, refundAmount, reason);

        // Then
        assertTrue(result.isPresent());
        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessRefund_NotFound() {
        // Given
        UUID paymentId = UUID.randomUUID();
        BigDecimal refundAmount = new BigDecimal("250.00");
        String reason = "Partial refund requested";

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When
        Optional<Payment> result = paymentService.processRefund(paymentId, refundAmount, reason);

        // Then
        assertFalse(result.isPresent());
        verify(paymentRepository, times(1)).findById(paymentId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testProcessRefund_WithExcessiveAmount_ThrowsException() {
        // Given
        UUID paymentId = testPayment.getId();
        BigDecimal refundAmount = new BigDecimal("600.00");
        String reason = "Excessive refund amount";

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processRefund(paymentId, refundAmount, reason);
        });
    }

    @Test
    void testProcessRefund_WithNegativeAmount_ThrowsException() {
        // Given
        UUID paymentId = testPayment.getId();
        BigDecimal refundAmount = new BigDecimal("-100.00");
        String reason = "Invalid refund amount";

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processRefund(paymentId, refundAmount, reason);
        });
    }

    @Test
    void testGetPaymentsWithPagination_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Payment> payments = Arrays.asList(testPayment);
        Page<Payment> paymentPage = new PageImpl<>(payments, pageable, 1);
        
        when(paymentRepository.findAll(pageable)).thenReturn(paymentPage);

        // When
        Page<Payment> result = paymentService.getPaymentsWithPagination(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(paymentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetPaymentCount_Success() {
        // Given
        long expectedCount = 50L;
        when(paymentRepository.count()).thenReturn(expectedCount);

        // When
        long result = paymentService.getPaymentCount();

        // Then
        assertEquals(expectedCount, result);
        verify(paymentRepository, times(1)).count();
    }

    @Test
    void testGetPaymentsByStatusCount_Success() {
        // Given
        PaymentStatus status = PaymentStatus.COMPLETED;
        long expectedCount = 45L;
        when(paymentRepository.countByStatus(status)).thenReturn(expectedCount);

        // When
        long result = paymentService.getPaymentsByStatusCount(status);

        // Then
        assertEquals(expectedCount, result);
        verify(paymentRepository, times(1)).countByStatus(status);
    }

    @Test
    void testGetTotalRevenue_Success() {
        // Given
        BigDecimal expectedRevenue = new BigDecimal("25000.00");
        when(paymentRepository.sumAmountByStatus(PaymentStatus.COMPLETED)).thenReturn(expectedRevenue);

        // When
        BigDecimal result = paymentService.getTotalRevenue();

        // Then
        assertEquals(expectedRevenue, result);
        verify(paymentRepository, times(1)).sumAmountByStatus(PaymentStatus.COMPLETED);
    }

    @Test
    void testGetTotalRefundedAmount_Success() {
        // Given
        BigDecimal expectedRefunded = new BigDecimal("2500.00");
        when(paymentRepository.sumRefundedAmount()).thenReturn(expectedRefunded);

        // When
        BigDecimal result = paymentService.getTotalRefundedAmount();

        // Then
        assertEquals(expectedRefunded, result);
        verify(paymentRepository, times(1)).sumRefundedAmount();
    }

    @Test
    void testGetPaymentsByDateRange_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByCreatedAtBetween(startDate, endDate)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByCreatedAtBetween(startDate, endDate);
    }

    @Test
    void testGetPaymentsByGatewayTransactionId_Success() {
        // Given
        String gatewayTransactionId = "txn_123456789";
        when(paymentRepository.findByGatewayTransactionId(gatewayTransactionId)).thenReturn(Optional.of(testPayment));

        // When
        Optional<Payment> result = paymentService.getPaymentByGatewayTransactionId(gatewayTransactionId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPayment.getGatewayTransactionId(), result.get().getGatewayTransactionId());
        verify(paymentRepository, times(1)).findByGatewayTransactionId(gatewayTransactionId);
    }

    @Test
    void testGetPaymentsByGatewayTransactionId_NotFound() {
        // Given
        String gatewayTransactionId = "txn_nonexistent";

        when(paymentRepository.findByGatewayTransactionId(gatewayTransactionId)).thenReturn(Optional.empty());

        // When
        Optional<Payment> result = paymentService.getPaymentByGatewayTransactionId(gatewayTransactionId);

        // Then
        assertFalse(result.isPresent());
        verify(paymentRepository, times(1)).findByGatewayTransactionId(gatewayTransactionId);
    }

    @Test
    void testGetRefundableAmount_Success() {
        // Given
        UUID paymentId = testPayment.getId();
        BigDecimal expectedRefundable = new BigDecimal("500.00");

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // When
        BigDecimal result = paymentService.getRefundableAmount(paymentId);

        // Then
        assertEquals(expectedRefundable, result);
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetRefundableAmount_NotFound() {
        // Given
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When
        BigDecimal result = paymentService.getRefundableAmount(paymentId);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPaymentsByUserAndStatus_Success() {
        // Given
        UUID userId = testUser.getId();
        PaymentStatus status = PaymentStatus.COMPLETED;
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByBookingUserIdAndStatus(userId, status)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByUserAndStatus(userId, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByBookingUserIdAndStatus(userId, status);
    }

    @Test
    void testGetPaymentsByMethodAndStatus_Success() {
        // Given
        PaymentMethod method = PaymentMethod.CARD;
        PaymentStatus status = PaymentStatus.COMPLETED;
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentRepository.findByPaymentMethodAndStatus(method, status)).thenReturn(payments);

        // When
        List<Payment> result = paymentService.getPaymentsByMethodAndStatus(method, status);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findByPaymentMethodAndStatus(method, status);
    }
}
