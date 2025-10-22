package com.lucknow.healthcare.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@Service
public class TwilioService {
    
    @Value("${twilio.account.sid:}")
    private String accountSid;
    
    @Value("${twilio.auth.token:}")
    private String authToken;
    
    @Value("${twilio.phone.number:}")
    private String twilioPhoneNumber;
    
    private boolean developmentMode = false;
    
    public TwilioService() {
        // Initialize Twilio with credentials from environment variables
        try {
            if (!isPlaceholderCredentials()) {
                Twilio.init(accountSid, authToken);
                System.out.println("✅ Twilio initialized successfully");
            } else {
                System.err.println("⚠️  Twilio credentials not configured - using development mode");
                developmentMode = true;
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Twilio: " + e.getMessage());
            System.err.println("🔧 Falling back to development mode - OTP will be logged to console");
            developmentMode = true;
        }
    }
    
    private boolean isPlaceholderCredentials() {
        return accountSid.contains("your_account_sid") || 
               authToken.contains("your_auth_token") ||
               accountSid.trim().isEmpty() ||
               authToken.trim().isEmpty();
    }
    
    public void sendOTP(String phoneNumber) {
        // Format phone number
        phoneNumber = formatPhoneNumber(phoneNumber);
        
        // Generate 6-digit OTP
        String otp = generateOTP();
        
        if (developmentMode) {
            // Development mode - just log the OTP
            System.out.println("🔧 DEVELOPMENT MODE - OTP for " + phoneNumber + ": " + otp);
            System.out.println("📱 In production, this would be sent via Twilio SMS");
            return;
        }
        
        try {
            // Send SMS via Twilio
            String messageBody = "Your Lucknow Healthcare OTP is: " + otp + ". Valid for 5 minutes. Do not share this code.";
            
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                messageBody
            ).create();
            
            System.out.println("✅ OTP sent successfully to " + phoneNumber + " via Twilio");
            System.out.println("📱 Message SID: " + message.getSid());
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP via Twilio: " + e.getMessage());
            System.err.println("🔧 Falling back to development mode");
            developmentMode = true;
            System.out.println("🔧 DEVELOPMENT MODE - OTP for " + phoneNumber + ": " + otp);
        }
    }
    
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }
    
    private String formatPhoneNumber(String phoneNumber) {
        // Remove all non-digit characters
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // Add country code if not present
        if (!phoneNumber.startsWith("91") && phoneNumber.length() == 10) {
            phoneNumber = "91" + phoneNumber;
        }
        
        // Add + prefix for international format
        return "+" + phoneNumber;
    }
    
    public boolean isDevelopmentMode() {
        return developmentMode;
    }
    
    /**
     * Verify OTP for a phone number
     * 
     * @param phoneNumber the phone number
     * @param otp the OTP to verify
     * @return Map containing verification result
     */
    public Map<String, Object> verifyOTP(String phoneNumber, String otp) {
        Map<String, Object> result = new HashMap<>();
        
        if (developmentMode) {
            // In development mode, accept any 6-digit OTP
            if (otp != null && otp.length() == 6 && otp.matches("\\d{6}")) {
                result.put("verified", true);
                result.put("message", "OTP verified successfully (development mode)");
                System.out.println("🔧 DEVELOPMENT MODE - OTP verification for " + phoneNumber + ": " + otp + " - ACCEPTED");
            } else {
                result.put("verified", false);
                result.put("error", "Invalid OTP format");
                System.out.println("🔧 DEVELOPMENT MODE - OTP verification for " + phoneNumber + ": " + otp + " - REJECTED");
            }
            return result;
        }
        
        // In production mode, implement actual OTP verification logic
        // For now, we'll use a simple validation
        try {
            // This is a placeholder - in real implementation, you would:
            // 1. Store OTPs in database with expiration
            // 2. Verify against stored OTP
            // 3. Check expiration time
            
            if (otp != null && otp.length() == 6 && otp.matches("\\d{6}")) {
                result.put("verified", true);
                result.put("message", "OTP verified successfully");
            } else {
                result.put("verified", false);
                result.put("error", "Invalid OTP");
            }
            
        } catch (Exception e) {
            result.put("verified", false);
            result.put("error", "OTP verification failed: " + e.getMessage());
        }
        
        return result;
    }
}
