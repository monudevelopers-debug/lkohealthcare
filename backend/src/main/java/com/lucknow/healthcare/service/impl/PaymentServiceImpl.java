package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.Booking;
import com.lucknow.healthcare.entity.Payment;
import com.lucknow.healthcare.enums.PaymentMethod;
import com.lucknow.healthcare.enums.PaymentStatus;
import com.lucknow.healthcare.repository.PaymentRepository;
import com.lucknow.healthcare.service.interfaces.PaymentService;
import com.lucknow.healthcare.service.interfaces.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for Payment entity operations (Phase 1.5)
 * 
 * Implements business logic for payment management including
 * payment processing, refunds, and transaction tracking.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingService bookingService;
    
    @Override
    public Payment createPayment(Payment payment) {
        // Validate booking exists
        if (payment.getBooking() == null || payment.getBooking().getId() == null) {
            throw new IllegalArgumentException("Booking is required for payment");
        }
        
        Optional<Booking> bookingOpt = bookingService.findById(payment.getBooking().getId());
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found with ID: " + payment.getBooking().getId());
        }
        
        // Check if payment already exists for this booking
        if (paymentRepository.findByBookingId(payment.getBooking().getId()).isPresent()) {
            throw new IllegalArgumentException("Payment already exists for booking: " + payment.getBooking().getId());
        }
        
        // Set default values
        payment.setStatus(PaymentStatus.PENDING);
        
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findById(UUID id) {
        return paymentRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
    
    @Override
    public Payment updatePayment(Payment payment) {
        if (!paymentRepository.existsById(payment.getId())) {
            throw new IllegalArgumentException("Payment not found with ID: " + payment.getId());
        }
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment updatePaymentStatus(UUID id, PaymentStatus status) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        Payment payment = paymentOpt.get();
        payment.setStatus(status);
        
        if (status == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
        }
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment processPayment(UUID id, String transactionId, String gatewayResponse) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        Payment payment = paymentOpt.get();
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment cannot be processed in current status: " + payment.getStatus());
        }
        
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(transactionId);
        payment.setGatewayResponse(gatewayResponse);
        payment.setPaidAt(LocalDateTime.now());
        
        return paymentRepository.save(payment);
    }
    
    @Override
    public Payment processRefund(UUID id, BigDecimal refundAmount) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        Payment payment = paymentOpt.get();
        
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalArgumentException("Only paid payments can be refunded");
        }
        
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0 || refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException("Invalid refund amount");
        }
        
        if (payment.processRefund(refundAmount)) {
            return paymentRepository.save(payment);
        } else {
            throw new IllegalArgumentException("Refund processing failed");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByMethod(method);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return paymentRepository.findByCreatedAtBetween(startDateTime, endDateTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByStatus(status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByMethod(PaymentMethod method, Pageable pageable) {
        return paymentRepository.findByMethod(method, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.countByMethod(method);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalAmountByStatus(PaymentStatus status) {
        return paymentRepository.calculateTotalAmountByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalAmountByMethod(PaymentMethod method) {
        return paymentRepository.calculateTotalAmountByMethod(method);
    }
    
    @Override
    @Transactional(readOnly = true)
    public String generateInvoice(UUID id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        return paymentOpt.get().generateInvoice();
    }
    
    @Override
    public boolean deletePayment(UUID id) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found with ID: " + id);
        }
        
        Payment payment = paymentOpt.get();
        payment.setStatus(PaymentStatus.FAILED);
        
        paymentRepository.save(payment);
        return true;
    }
}
