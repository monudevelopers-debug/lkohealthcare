package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.Payment;
import com.lucknow.healthcare.enums.PaymentMethod;
import com.lucknow.healthcare.enums.PaymentStatus;
import com.lucknow.healthcare.service.interfaces.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for Payment entity operations
 * 
 * Provides REST endpoints for payment management including
 * CRUD operations, payment processing, refunds, and transaction tracking.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * Create a new payment
     * 
     * @param payment the payment to create
     * @return ResponseEntity containing the created payment
     */
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        try {
            Payment createdPayment = paymentService.createPayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get payment by ID
     * 
     * @param id the payment ID
     * @return ResponseEntity containing the payment if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable UUID id) {
        Optional<Payment> paymentOpt = paymentService.findById(id);
        return paymentOpt.map(payment -> ResponseEntity.ok(payment))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get payment by booking ID
     * 
     * @param bookingId the booking ID
     * @return ResponseEntity containing the payment if found
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Payment> getPaymentByBookingId(@PathVariable UUID bookingId) {
        Optional<Payment> paymentOpt = paymentService.findByBookingId(bookingId);
        return paymentOpt.map(payment -> ResponseEntity.ok(payment))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get payment by transaction ID
     * 
     * @param transactionId the transaction ID
     * @return ResponseEntity containing the payment if found
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        Optional<Payment> paymentOpt = paymentService.getPaymentByTransactionId(transactionId);
        return paymentOpt.map(payment -> ResponseEntity.ok(payment))
                         .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update payment
     * 
     * @param id the payment ID
     * @param payment the updated payment information
     * @return ResponseEntity containing the updated payment
     */
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable UUID id, @RequestBody Payment payment) {
        try {
            payment.setId(id);
            Payment updatedPayment = paymentService.updatePayment(payment);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update payment status
     * 
     * @param id the payment ID
     * @param status the new payment status
     * @return ResponseEntity containing the updated payment
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable UUID id, @RequestParam PaymentStatus status) {
        try {
            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Process payment
     * 
     * @param id the payment ID
     * @param request the payment processing request containing transactionId and gatewayResponse
     * @return ResponseEntity containing the processed payment
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<Payment> processPayment(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        try {
            String transactionId = request.get("transactionId");
            String gatewayResponse = request.get("gatewayResponse");
            Payment processedPayment = paymentService.processPayment(id, transactionId, gatewayResponse);
            return ResponseEntity.ok(processedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Process refund
     * 
     * @param id the payment ID
     * @param request the refund request containing refundAmount
     * @return ResponseEntity containing the updated payment
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<Payment> processRefund(
            @PathVariable UUID id,
            @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal refundAmount = request.get("refundAmount");
            Payment refundedPayment = paymentService.processRefund(id, refundAmount);
            return ResponseEntity.ok(refundedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all payments with pagination
     * 
     * @param page the page number (default: 0)
     * @param size the page size (default: 20, max: 100)
     * @param sortBy the field to sort by (default: createdAt)
     * @param sortDir the sort direction (default: desc)
     * @return ResponseEntity containing page of payments
     */
    @GetMapping
    public ResponseEntity<Page<Payment>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            size = Math.min(size, 100);
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Payment> payments = paymentService.getAllPayments(pageable);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get payments by status with pagination
     * 
     * @param status the payment status
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return ResponseEntity containing page of payments
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Payment>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Payment> payments = paymentService.getPaymentsByStatus(status, pageable);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get payments by method with pagination
     * 
     * @param method the payment method
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @return ResponseEntity containing page of payments
     */
    @GetMapping("/method/{method}")
    public ResponseEntity<Page<Payment>> getPaymentsByMethod(
            @PathVariable PaymentMethod method,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Payment> payments = paymentService.getPaymentsByMethod(method, pageable);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get payments by date range
     * 
     * @param startDate the start date (format: yyyy-MM-ddTHH:mm:ss)
     * @param endDate the end date (format: yyyy-MM-ddTHH:mm:ss)
     * @return ResponseEntity containing list of payments
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate);
            List<Payment> payments = paymentService.getPaymentsByDateRange(startDateTime, endDateTime);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Generate invoice for payment
     * 
     * @param id the payment ID
     * @return ResponseEntity containing the invoice number
     */
    @GetMapping("/{id}/invoice")
    public ResponseEntity<Map<String, String>> generateInvoice(@PathVariable UUID id) {
        try {
            String invoiceNumber = paymentService.generateInvoice(id);
            Map<String, String> response = new HashMap<>();
            response.put("invoiceNumber", invoiceNumber);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get payment statistics
     * 
     * @return ResponseEntity containing payment statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPaymentStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Count by status
            stats.put("pendingCount", paymentService.countPaymentsByStatus(PaymentStatus.PENDING));
            stats.put("paidCount", paymentService.countPaymentsByStatus(PaymentStatus.PAID));
            stats.put("failedCount", paymentService.countPaymentsByStatus(PaymentStatus.FAILED));
            stats.put("refundedCount", paymentService.countPaymentsByStatus(PaymentStatus.REFUNDED));
            
            // Total amounts
            stats.put("totalPaid", paymentService.calculateTotalAmountByStatus(PaymentStatus.PAID));
            stats.put("totalRefunded", paymentService.calculateTotalAmountByStatus(PaymentStatus.REFUNDED));
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete payment
     * 
     * @param id the payment ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        try {
            boolean deleted = paymentService.deletePayment(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

