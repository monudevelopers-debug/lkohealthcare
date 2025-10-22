package com.lucknow.healthcare.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PostConstruct;

@Service
public class TwilioService {
    
    @Value("${TWILIO_ACCOUNT_SID:}")
    private String accountSid;
    
    @Value("${TWILIO_AUTH_TOKEN:}")
    private String authToken;
    
    @Value("${TWILIO_PHONE_NUMBER:}")
    private String twilioPhoneNumber;
    
    @Value("${TWILIO_DEVELOPMENT_MODE:true}")
    private boolean developmentMode;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @PostConstruct
    public void init() {
        // Initialize Twilio with credentials from .env file
        try {
            if (!isPlaceholderCredentials()) {
                Twilio.init(accountSid, authToken);
                System.out.println("✅ Twilio initialized successfully with credentials from .env file");
                System.out.println("📱 Twilio Phone Number: " + twilioPhoneNumber);
                System.out.println("🔧 Development Mode: " + developmentMode);
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
        
        // Store OTP in Redis with 5-minute expiration
        String otpKey = "otp:" + phoneNumber;
        try {
            redisTemplate.opsForValue().set(otpKey, otp, 5, TimeUnit.MINUTES);
            System.out.println("✅ OTP stored in Redis for " + phoneNumber + " with 5-minute expiration");
        } catch (Exception e) {
            System.err.println("❌ Failed to store OTP in Redis: " + e.getMessage());
        }
        
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
        
        // Format phone number
        phoneNumber = formatPhoneNumber(phoneNumber);
        
        try {
            // Get stored OTP from Redis
            String otpKey = "otp:" + phoneNumber;
            String storedOTP = redisTemplate.opsForValue().get(otpKey);
            
            if (storedOTP == null) {
                result.put("verified", false);
                result.put("error", "OTP not found or expired. Please request a new OTP.");
                System.out.println("❌ OTP verification failed for " + phoneNumber + " - OTP not found or expired");
                return result;
            }
            
            // Check if OTP matches
            if (storedOTP.equals(otp)) {
                // OTP is correct, remove it from Redis to prevent reuse
                redisTemplate.delete(otpKey);
                result.put("verified", true);
                result.put("message", "OTP verified successfully");
                System.out.println("✅ OTP verification successful for " + phoneNumber);
            } else {
                result.put("verified", false);
                result.put("error", "Invalid OTP. Please check and try again.");
                System.out.println("❌ OTP verification failed for " + phoneNumber + " - Invalid OTP");
            }
            
        } catch (Exception e) {
            result.put("verified", false);
            result.put("error", "OTP verification failed: " + e.getMessage());
            System.err.println("❌ OTP verification error for " + phoneNumber + ": " + e.getMessage());
        }
        
        return result;
    }
}
