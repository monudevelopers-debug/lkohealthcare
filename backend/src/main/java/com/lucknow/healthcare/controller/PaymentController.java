package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.payment.PaymentGateway;
import com.lucknow.healthcare.payment.PaytmPaymentGateway;
import com.lucknow.healthcare.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Payment operations
 * 
 * Handles payment initiation, verification, and callbacks from payment gateways
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private PaymentGateway paymentGateway;
    
    @Value("${payment.gateway.mode:DUMMY}")
    private String gatewayMode;
    
    /**
     * Initiate a payment
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentInitiationRequest request) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            
            logger.info("Initiating payment - Gateway: {}, Booking: {}, Amount: {}", 
                       gatewayMode, request.getBookingId(), request.getAmount());
            
            // Create payment request
            PaymentGateway.PaymentRequest paymentRequest = new PaymentGateway.PaymentRequest();
            paymentRequest.setBookingId(UUID.fromString(request.getBookingId()));
            paymentRequest.setCustomerId(customerId);
            paymentRequest.setAmount(request.getAmount());
            paymentRequest.setPaymentMethod(request.getPaymentMethod());
            paymentRequest.setCurrency("INR");
            
            // Initiate payment through gateway
            PaymentGateway.PaymentInitiationResponse response = paymentGateway.initiatePayment(paymentRequest);
            
            // For Paytm, also return the form parameters
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("transactionId", response.getTransactionId());
            responseData.put("orderId", response.getOrderId());
            responseData.put("status", response.getStatus());
            responseData.put("amount", response.getAmount());
            responseData.put("currency", response.getCurrency());
            responseData.put("message", response.getMessage());
            responseData.put("gatewayMode", gatewayMode);
            
            // If Paytm, include parameters for frontend form submission
            if ("PAYTM".equals(gatewayMode) && paymentGateway instanceof PaytmPaymentGateway) {
                PaytmPaymentGateway paytmGateway = (PaytmPaymentGateway) paymentGateway;
                Map<String, String> paytmParams = paytmGateway.getPaytmParameters(response.getOrderId());
                responseData.put("paytmParams", paytmParams);
                responseData.put("paytmUrl", paytmGateway.getTransactionUrl());
            }
            
            return ResponseEntity.ok(responseData);
            
        } catch (SecurityException e) {
            logger.error("Authentication error in payment initiation", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error initiating payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error initiating payment: " + e.getMessage());
        }
    }
    
    /**
     * Verify payment status
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        try {
            logger.info("Verifying payment: {}", request.getTransactionId());
            
            PaymentGateway.PaymentVerificationResponse response = 
                paymentGateway.verifyPayment(request.getTransactionId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }
    
    /**
     * Paytm callback handler
     */
    @PostMapping("/paytm/callback")
    public ResponseEntity<?> paytmCallback(@RequestParam Map<String, String> paytmResponse) {
        try {
            logger.info("Received Paytm callback: {}", paytmResponse);
            
            String orderId = paytmResponse.get("ORDERID");
            String transactionId = paytmResponse.get("TXNID");
            String status = paytmResponse.get("STATUS");
            String respCode = paytmResponse.get("RESPCODE");
            String checksumHash = paytmResponse.get("CHECKSUMHASH");
            
            // Verify checksum
            if (paymentGateway instanceof PaytmPaymentGateway) {
                PaytmPaymentGateway paytmGateway = (PaytmPaymentGateway) paymentGateway;
                
                Map<String, String> paramsForVerification = new HashMap<>(paytmResponse);
                boolean isValid = paytmGateway.verifyChecksum(paramsForVerification, checksumHash);
                
                if (!isValid) {
                    logger.error("Invalid checksum in Paytm callback for order: {}", orderId);
                    return ResponseEntity.badRequest().body("Invalid checksum");
                }
                
                // Update transaction status
                paytmGateway.updateTransactionStatus(orderId, transactionId, status, respCode);
            }
            
            // Return success page or redirect
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("transactionId", transactionId);
            response.put("status", status);
            response.put("message", "TXN_SUCCESS".equals(status) ? "Payment successful!" : "Payment failed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing Paytm callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing callback: " + e.getMessage());
        }
    }
    
    /**
     * Process refund
     */
    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<?> processRefund(
            @PathVariable String transactionId,
            @RequestBody RefundRequest request) {
        try {
            UUID customerId = SecurityUtils.getCurrentUserId();
            
            logger.info("Processing refund for transaction: {}, amount: {}", 
                       transactionId, request.getAmount());
            
            PaymentGateway.RefundResponse response = paymentGateway.processRefund(
                transactionId, 
                request.getAmount(), 
                request.getReason()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing refund", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing refund: " + e.getMessage());
        }
    }
    
    /**
     * Get payment status
     */
    @GetMapping("/{transactionId}/status")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String transactionId) {
        try {
            PaymentGateway.PaymentStatus status = paymentGateway.getPaymentStatus(transactionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transactionId);
            response.put("status", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching payment status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching status: " + e.getMessage());
        }
    }
    
    // Request DTOs
    
    static class PaymentInitiationRequest {
        private String bookingId;
        private BigDecimal amount;
        private String paymentMethod;
        
        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    static class PaymentVerificationRequest {
        private String transactionId;
        private String orderId;
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
    }
    
    static class RefundRequest {
        private BigDecimal amount;
        private String reason;
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
