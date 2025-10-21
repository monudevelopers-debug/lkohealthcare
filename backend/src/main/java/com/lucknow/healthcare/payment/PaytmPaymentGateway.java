package com.lucknow.healthcare.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Paytm Payment Gateway Implementation
 * 
 * Integrates with Paytm's payment processing system for Indian market.
 * Supports both test and production environments.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@ConditionalOnProperty(name = "payment.gateway.mode", havingValue = "PAYTM")
public class PaytmPaymentGateway implements PaymentGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(PaytmPaymentGateway.class);
    
    @Value("${payment.paytm.merchant-id}")
    private String merchantId;
    
    @Value("${payment.paytm.merchant-key}")
    private String merchantKey;
    
    @Value("${payment.paytm.website}")
    private String website;
    
    @Value("${payment.paytm.industry-type}")
    private String industryType;
    
    @Value("${payment.paytm.channel-id-web}")
    private String channelIdWeb;
    
    @Value("${payment.paytm.channel-id-mobile}")
    private String channelIdMobile;
    
    @Value("${payment.paytm.callback-url}")
    private String callbackUrl;
    
    @Value("${payment.paytm.transaction-url}")
    private String transactionUrl;
    
    @Value("${payment.paytm.status-query-url}")
    private String statusQueryUrl;
    
    // In-memory storage for tracking transactions (use Redis in production)
    private final Map<String, PaytmTransaction> transactions = new HashMap<>();
    
    @Override
    public PaymentInitiationResponse initiatePayment(PaymentRequest request) {
        logger.info("Initiating Paytm payment for booking: {}, amount: {}", 
                   request.getBookingId(), request.getAmount());
        
        try {
            // Generate unique order ID
            String orderId = "LKO_" + request.getBookingId().toString().substring(0, 8).toUpperCase() 
                           + "_" + System.currentTimeMillis();
            
            // Calculate amount in paisa (Paytm expects amount in currency subunit)
            String amountStr = request.getAmount().toString();
            
            // Create Paytm parameters
            TreeMap<String, String> paytmParams = new TreeMap<>();
            paytmParams.put("MID", merchantId);
            paytmParams.put("ORDER_ID", orderId);
            paytmParams.put("CUST_ID", request.getCustomerId().toString());
            paytmParams.put("INDUSTRY_TYPE_ID", industryType);
            paytmParams.put("CHANNEL_ID", channelIdWeb);
            paytmParams.put("TXN_AMOUNT", amountStr);
            paytmParams.put("WEBSITE", website);
            paytmParams.put("CALLBACK_URL", callbackUrl);
            
            // Generate checksum
            String checksum = generateChecksum(paytmParams, merchantKey);
            paytmParams.put("CHECKSUMHASH", checksum);
            
            // Store transaction for verification
            PaytmTransaction transaction = new PaytmTransaction();
            transaction.setOrderId(orderId);
            transaction.setBookingId(request.getBookingId());
            transaction.setCustomerId(request.getCustomerId());
            transaction.setAmount(request.getAmount());
            transaction.setStatus("INITIATED");
            transaction.setChecksum(checksum);
            transaction.setTimestamp(LocalDateTime.now());
            
            transactions.put(orderId, transaction);
            
            // Create response
            PaymentInitiationResponse response = new PaymentInitiationResponse();
            response.setTransactionId(orderId); // Using orderId as transactionId initially
            response.setOrderId(orderId);
            response.setStatus("INITIATED");
            response.setAmount(request.getAmount());
            response.setCurrency("INR");
            response.setMessage("Payment initiated successfully. Redirect user to Paytm.");
            response.setTimestamp(LocalDateTime.now());
            
            logger.info("Paytm payment initiated successfully with order ID: {}", orderId);
            logger.info("Transaction URL: {}", transactionUrl);
            logger.info("Paytm Parameters: {}", paytmParams);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error initiating Paytm payment", e);
            throw new RuntimeException("Failed to initiate Paytm payment", e);
        }
    }
    
    @Override
    public PaymentVerificationResponse verifyPayment(String orderId) {
        logger.info("Verifying Paytm payment for order: {}", orderId);
        
        try {
            PaytmTransaction transaction = transactions.get(orderId);
            
            if (transaction == null) {
                PaymentVerificationResponse response = new PaymentVerificationResponse();
                response.setOrderId(orderId);
                response.setStatus("NOT_FOUND");
                response.setVerified(false);
                response.setMessage("Transaction not found");
                return response;
            }
            
            // In production, you would call Paytm's status query API here
            // For now, we'll return the stored transaction status
            
            PaymentVerificationResponse response = new PaymentVerificationResponse();
            response.setTransactionId(transaction.getTransactionId());
            response.setOrderId(orderId);
            response.setStatus(transaction.getStatus());
            response.setAmount(transaction.getAmount());
            response.setPaymentMethod("PAYTM");
            response.setVerified("SUCCESS".equals(transaction.getStatus()));
            response.setMessage("Payment " + (response.isVerified() ? "successful" : "pending/failed"));
            response.setTimestamp(transaction.getTimestamp());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error verifying Paytm payment", e);
            PaymentVerificationResponse response = new PaymentVerificationResponse();
            response.setOrderId(orderId);
            response.setStatus("ERROR");
            response.setVerified(false);
            response.setMessage("Error verifying payment: " + e.getMessage());
            return response;
        }
    }
    
    @Override
    public RefundResponse processRefund(String transactionId, BigDecimal amount, String reason) {
        logger.info("Processing Paytm refund for transaction: {}, amount: {}", transactionId, amount);
        
        try {
            // Find transaction by transaction ID
            PaytmTransaction transaction = transactions.values().stream()
                .filter(t -> transactionId.equals(t.getTransactionId()))
                .findFirst()
                .orElse(null);
            
            if (transaction == null || !"SUCCESS".equals(transaction.getStatus())) {
                RefundResponse response = new RefundResponse();
                response.setSuccess(false);
                response.setMessage("Cannot refund - Transaction not found or not successful");
                return response;
            }
            
            if (amount.compareTo(transaction.getAmount()) > 0) {
                RefundResponse response = new RefundResponse();
                response.setSuccess(false);
                response.setMessage("Refund amount cannot exceed original payment amount");
                return response;
            }
            
            // Generate refund ID
            String refundId = "REFUND_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            
            // In production, call Paytm refund API here
            // For test mode, we'll simulate success
            
            RefundResponse response = new RefundResponse();
            response.setSuccess(true);
            response.setRefundId(refundId);
            response.setAmount(amount);
            response.setStatus("PENDING"); // Paytm refunds are processed in 7 working days
            response.setMessage("Refund initiated successfully. Will be processed in 7 working days.");
            response.setProcessedAt(LocalDateTime.now());
            
            logger.info("Paytm refund initiated successfully: {}", refundId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing Paytm refund", e);
            RefundResponse response = new RefundResponse();
            response.setSuccess(false);
            response.setMessage("Error processing refund: " + e.getMessage());
            return response;
        }
    }
    
    @Override
    public PaymentStatus getPaymentStatus(String transactionId) {
        PaytmTransaction transaction = transactions.values().stream()
            .filter(t -> transactionId.equals(t.getTransactionId()))
            .findFirst()
            .orElse(null);
        
        if (transaction == null) {
            return PaymentStatus.FAILED;
        }
        
        return switch (transaction.getStatus()) {
            case "SUCCESS", "TXN_SUCCESS" -> PaymentStatus.SUCCESS;
            case "FAILED", "TXN_FAILURE" -> PaymentStatus.FAILED;
            case "PENDING", "INITIATED" -> PaymentStatus.PENDING;
            case "PROCESSING" -> PaymentStatus.PROCESSING;
            default -> PaymentStatus.FAILED;
        };
    }
    
    /**
     * Update transaction status after Paytm callback
     */
    public void updateTransactionStatus(String orderId, String paytmTransactionId, 
                                       String status, String responseCode) {
        PaytmTransaction transaction = transactions.get(orderId);
        if (transaction != null) {
            transaction.setTransactionId(paytmTransactionId);
            transaction.setStatus(status);
            transaction.setResponseCode(responseCode);
            logger.info("Updated transaction status: {} -> {}", orderId, status);
        }
    }
    
    /**
     * Get Paytm parameters for frontend
     */
    public Map<String, String> getPaytmParameters(String orderId) {
        PaytmTransaction transaction = transactions.get(orderId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found: " + orderId);
        }
        
        TreeMap<String, String> paytmParams = new TreeMap<>();
        paytmParams.put("MID", merchantId);
        paytmParams.put("ORDER_ID", orderId);
        paytmParams.put("CUST_ID", transaction.getCustomerId().toString());
        paytmParams.put("INDUSTRY_TYPE_ID", industryType);
        paytmParams.put("CHANNEL_ID", channelIdWeb);
        paytmParams.put("TXN_AMOUNT", transaction.getAmount().toString());
        paytmParams.put("WEBSITE", website);
        paytmParams.put("CALLBACK_URL", callbackUrl);
        
        try {
            String checksum = generateChecksum(paytmParams, merchantKey);
            paytmParams.put("CHECKSUMHASH", checksum);
        } catch (Exception e) {
            logger.error("Error generating checksum", e);
        }
        
        return paytmParams;
    }
    
    /**
     * Verify checksum from Paytm response
     */
    public boolean verifyChecksum(Map<String, String> paytmParams, String checksum) {
        try {
            TreeMap<String, String> params = new TreeMap<>(paytmParams);
            params.remove("CHECKSUMHASH");
            
            String generatedChecksum = generateChecksum(params, merchantKey);
            return generatedChecksum.equals(checksum);
        } catch (Exception e) {
            logger.error("Error verifying checksum", e);
            return false;
        }
    }
    
    /**
     * Get Paytm transaction URL for frontend
     */
    public String getTransactionUrl() {
        return transactionUrl;
    }
    
    /**
     * Generate Paytm checksum
     */
    private String generateChecksum(TreeMap<String, String> params, String merchantKey) throws Exception {
        StringBuilder allParams = new StringBuilder();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            allParams.append(entry.getValue()).append("|");
        }
        
        // Remove trailing pipe
        if (allParams.length() > 0) {
            allParams.setLength(allParams.length() - 1);
        }
        
        // Generate checksum using HMAC-SHA256
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(merchantKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(allParams.toString().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
    
    // Transaction storage class
    private static class PaytmTransaction {
        private String orderId;
        private String transactionId; // Paytm's transaction ID (after payment)
        private UUID bookingId;
        private UUID customerId;
        private BigDecimal amount;
        private String status;
        private String checksum;
        private String responseCode;
        private LocalDateTime timestamp;
        
        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public UUID getBookingId() { return bookingId; }
        public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
        
        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
        
        public String getResponseCode() { return responseCode; }
        public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}

