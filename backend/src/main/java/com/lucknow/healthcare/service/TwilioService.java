package com.lucknow.healthcare.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TwilioService {
    
    @Value("${twilio.account-sid}")
    private String accountSid;
    
    @Value("${twilio.auth-token}")
    private String authToken;
    
    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;
    
    @Value("${twilio.otp.expiry-minutes:5}")
    private int otpExpiryMinutes;
    
    @Value("${twilio.otp.max-attempts:3}")
    private int maxAttempts;
    
    // Store OTP with expiry (phone -> OTP data)
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    private static class OtpData {
        String otp;
        long expiryTime;
        int attempts;
        
        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
            this.attempts = 0;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
        
        boolean canAttempt() {
            return attempts < 3;
        }
        
        void incrementAttempts() {
            this.attempts++;
        }
    }
    
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
        System.out.println("Twilio initialized successfully with account SID: " + accountSid);
    }
    
    public void sendOTP(String phoneNumber) {
        // Format phone number
        phoneNumber = formatPhoneNumber(phoneNumber);
        
        // Generate 6-digit OTP
        String otp = generateOTP();
        
        // Store with expiry
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(otpExpiryMinutes);
        otpStorage.put(phoneNumber, new OtpData(otp, expiryTime));
        
        // Send SMS
        String messageBody = "Your Lucknow Healthcare OTP is: " + otp + ". Valid for " + otpExpiryMinutes + " minutes. Do not share with anyone.";
        
        try {
            Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                messageBody
            ).create();
            
            System.out.println("OTP sent successfully to " + phoneNumber + ". SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send OTP to " + phoneNumber + ": " + e.getMessage());
            otpStorage.remove(phoneNumber); // Remove if send failed
            throw new RuntimeException("Failed to send OTP. Please try again.", e);
        }
    }
    
    public Map<String, Object> verifyOTP(String phoneNumber, String otp) {
        phoneNumber = formatPhoneNumber(phoneNumber);
        
        OtpData otpData = otpStorage.get(phoneNumber);
        
        if (otpData == null) {
            return Map.of(
                "verified", false,
                "error", "No OTP found. Please request a new OTP."
            );
        }
        
        if (otpData.isExpired()) {
            otpStorage.remove(phoneNumber);
            return Map.of(
                "verified", false,
                "error", "OTP has expired. Please request a new OTP."
            );
        }
        
        if (!otpData.canAttempt()) {
            otpStorage.remove(phoneNumber);
            return Map.of(
                "verified", false,
                "error", "Too many incorrect attempts. Please request a new OTP."
            );
        }
        
        if (otpData.otp.equals(otp)) {
            otpStorage.remove(phoneNumber); // OTP used, remove it
            return Map.of(
                "verified", true,
                "message", "OTP verified successfully"
            );
        }
        
        // Wrong OTP
        otpData.incrementAttempts();
        int remainingAttempts = maxAttempts - otpData.attempts;
        
        if (remainingAttempts <= 0) {
            otpStorage.remove(phoneNumber);
            return Map.of(
                "verified", false,
                "error", "Too many incorrect attempts. Please request a new OTP."
            );
        }
        
        return Map.of(
            "verified", false,
            "error", "Invalid OTP. " + remainingAttempts + " attempts remaining."
        );
    }
    
    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        // If starts with 91 and has 12 digits, add +
        if (cleaned.startsWith("91") && cleaned.length() == 12) {
            return "+" + cleaned;
        }
        
        // If 10 digits, add +91
        if (cleaned.length() == 10) {
            return "+91" + cleaned;
        }
        
        // If already has +, return as is
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        
        // Otherwise, add + to the cleaned number
        return "+" + cleaned;
    }
    
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    public void clearOTP(String phoneNumber) {
        phoneNumber = formatPhoneNumber(phoneNumber);
        otpStorage.remove(phoneNumber);
    }
}

