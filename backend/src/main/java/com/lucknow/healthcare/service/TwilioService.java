package com.lucknow.healthcare.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

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
}
