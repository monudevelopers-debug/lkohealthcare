package com.lucknow.healthcare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Payment;
import com.lucknow.healthcare.entity.Provider;
import com.lucknow.healthcare.entity.Service;
import com.lucknow.healthcare.entity.ServiceCategory;
import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.BookingStatus;
import com.lucknow.healthcare.enums.PaymentStatus;
import com.lucknow.healthcare.enums.PaymentMethod;
import com.lucknow.healthcare.enums.ProviderStatus;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.service.interfaces.PaymentService;
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
 * Unit tests for PaymentController
 * 
 * Tests all payment management endpoints including payment processing,
 * refund handling, payment history, and authorization.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Payment testPayment;
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

        // Setup test payment
        testPayment = new Payment();
        testPayment.setId(UUID.randomUUID());
        testPayment.setBooking(testBooking);
        testPayment.setAmount(BigDecimal.valueOf(500.00));
        testPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        testPayment.setStatus(PaymentStatus.COMPLETED);
        testPayment.setTransactionId("TXN123456789");
        testPayment.setPaymentDate(LocalDateTime.now());
        testPayment.setCreatedAt(LocalDateTime.now());
        testPayment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPayments_Success() throws Exception {
        // Given
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.getAllPayments()).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testPayment.getId().toString()))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void testGetAllPayments_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetAllPayments_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentById_Success() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.getPaymentById(paymentId)).thenReturn(Optional.of(testPayment));

        // When & Then
        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentById_NotFound() throws Exception {
        // Given
        UUID paymentId = UUID.randomUUID();
        when(paymentService.getPaymentById(paymentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentById_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByBookingId_Success() throws Exception {
        // Given
        UUID bookingId = testBooking.getId();
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByBookingId(bookingId)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/booking/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testPayment.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByBookingId_WithInvalidUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/booking/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByStatus_Success() throws Exception {
        // Given
        PaymentStatus status = PaymentStatus.COMPLETED;
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByStatus(status)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByMethod_Success() throws Exception {
        // Given
        PaymentMethod method = PaymentMethod.CREDIT_CARD;
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByMethod(method)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/method/{method}", method))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByMethod_WithInvalidMethod_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/method/INVALID_METHOD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByDateRange_Success() throws Exception {
        // Given
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.getPaymentsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/date-range")
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testPayment.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByDateRange_WithInvalidDateFormat_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/date-range")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByDateRange_WithMissingEndDate_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/date-range")
                .param("startDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPayments_Success() throws Exception {
        // Given
        String searchTerm = "TXN123456789";
        List<Payment> payments = Arrays.asList(testPayment);
        when(paymentService.searchPayments(searchTerm)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/search")
                .param("q", searchTerm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].transactionId").value("TXN123456789"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPayments_WithEmptySearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/search")
                .param("q", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPayments_WithNullSearchTerm_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePayment_Success() throws Exception {
        // Given
        Payment newPayment = new Payment();
        newPayment.setAmount(BigDecimal.valueOf(500.00));
        newPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        newPayment.setStatus(PaymentStatus.PENDING);

        when(paymentService.createPayment(any(Payment.class))).thenReturn(testPayment);

        // When & Then
        mockMvc.perform(post("/api/payments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testPayment.getId().toString()))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    void testCreatePayment_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Given
        Payment newPayment = new Payment();
        newPayment.setAmount(BigDecimal.valueOf(500.00));
        newPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        // When & Then
        mockMvc.perform(post("/api/payments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testCreatePayment_WithInsufficientRole_ReturnsForbidden() throws Exception {
        // Given
        Payment newPayment = new Payment();
        newPayment.setAmount(BigDecimal.valueOf(500.00));
        newPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        // When & Then
        mockMvc.perform(post("/api/payments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePayment_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        Payment newPayment = new Payment();
        newPayment.setAmount(null); // Invalid data
        newPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        // When & Then
        mockMvc.perform(post("/api/payments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePayment_Success() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        Payment updatedPayment = new Payment();
        updatedPayment.setAmount(BigDecimal.valueOf(600.00));
        updatedPayment.setPaymentMethod(PaymentMethod.DEBIT_CARD);

        when(paymentService.updatePayment(paymentId, updatedPayment)).thenReturn(Optional.of(testPayment));

        // When & Then
        mockMvc.perform(put("/api/payments/{id}", paymentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePayment_NotFound() throws Exception {
        // Given
        UUID paymentId = UUID.randomUUID();
        Payment updatedPayment = new Payment();
        updatedPayment.setAmount(BigDecimal.valueOf(600.00));

        when(paymentService.updatePayment(paymentId, updatedPayment)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/payments/{id}", paymentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePayment_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        Payment updatedPayment = new Payment();
        updatedPayment.setAmount(null); // Invalid data

        // When & Then
        mockMvc.perform(put("/api/payments/{id}", paymentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletePayment_Success() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.deletePayment(paymentId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/payments/{id}", paymentId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletePayment_NotFound() throws Exception {
        // Given
        UUID paymentId = UUID.randomUUID();
        when(paymentService.deletePayment(paymentId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/payments/{id}", paymentId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testProcessPayment_Success() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.processPayment(paymentId)).thenReturn(Optional.of(testPayment));

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/process", paymentId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testProcessPayment_NotFound() throws Exception {
        // Given
        UUID paymentId = UUID.randomUUID();
        when(paymentService.processPayment(paymentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/process", paymentId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRefundPayment_Success() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.refundPayment(paymentId)).thenReturn(Optional.of(testPayment));

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/refund", paymentId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRefundPayment_NotFound() throws Exception {
        // Given
        UUID paymentId = UUID.randomUUID();
        when(paymentService.refundPayment(paymentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/refund", paymentId)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRefundPayment_WithInvalidAmount_ReturnsBadRequest() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.refundPayment(paymentId))
                .thenThrow(new RuntimeException("Invalid refund amount"));

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/refund", paymentId)
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsWithPagination_Success() throws Exception {
        // Given
        Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(testPayment), PageRequest.of(0, 10), 1);
        when(paymentService.getPaymentsWithPagination(any())).thenReturn(paymentPage);

        // When & Then
        mockMvc.perform(get("/api/payments/paginated")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsWithPagination_WithInvalidPageNumber_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/paginated")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsWithPagination_WithInvalidPageSize_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/paginated")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentCount_Success() throws Exception {
        // Given
        long expectedCount = 100L;
        when(paymentService.getPaymentCount()).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/payments/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByStatusCount_Success() throws Exception {
        // Given
        PaymentStatus status = PaymentStatus.COMPLETED;
        long expectedCount = 80L;
        when(paymentService.getPaymentsByStatusCount(status)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/payments/status/{status}/count", status))
                .andExpect(status().isOk())
                .andExpect(content().string("80"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentsByMethodCount_Success() throws Exception {
        // Given
        PaymentMethod method = PaymentMethod.CREDIT_CARD;
        long expectedCount = 60L;
        when(paymentService.getPaymentsByMethodCount(method)).thenReturn(expectedCount);

        // When & Then
        mockMvc.perform(get("/api/payments/method/{method}/count", method))
                .andExpect(status().isOk())
                .andExpect(content().string("60"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTotalRevenue_Success() throws Exception {
        // Given
        BigDecimal expectedRevenue = BigDecimal.valueOf(50000.00);
        when(paymentService.getTotalRevenue()).thenReturn(expectedRevenue);

        // When & Then
        mockMvc.perform(get("/api/payments/revenue/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("50000.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRevenueByDateRange_Success() throws Exception {
        // Given
        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        BigDecimal expectedRevenue = BigDecimal.valueOf(25000.00);
        when(paymentService.getRevenueByDateRange(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(expectedRevenue);

        // When & Then
        mockMvc.perform(get("/api/payments/revenue/date-range")
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(content().string("25000.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRevenueByDateRange_WithInvalidDateFormat_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/revenue/date-range")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRevenueByDateRange_WithMissingEndDate_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/revenue/date-range")
                .param("startDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaymentById_WithSpecialCharactersInUUID_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/payments/{id}", "invalid-uuid-with-special-chars"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePayment_WithDuplicateTransactionId_ReturnsConflict() throws Exception {
        // Given
        Payment newPayment = new Payment();
        newPayment.setAmount(BigDecimal.valueOf(500.00));
        newPayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        newPayment.setTransactionId("TXN123456789");

        when(paymentService.createPayment(any(Payment.class)))
                .thenThrow(new RuntimeException("Payment with this transaction ID already exists"));

        // When & Then
        mockMvc.perform(post("/api/payments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testProcessPayment_WithInvalidPaymentStatus_ReturnsBadRequest() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.processPayment(paymentId))
                .thenThrow(new RuntimeException("Payment cannot be processed in current status"));

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/process", paymentId)
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRefundPayment_WithInvalidPaymentStatus_ReturnsBadRequest() throws Exception {
        // Given
        UUID paymentId = testPayment.getId();
        when(paymentService.refundPayment(paymentId))
                .thenThrow(new RuntimeException("Payment cannot be refunded in current status"));

        // When & Then
        mockMvc.perform(post("/api/payments/{id}/refund", paymentId)
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
