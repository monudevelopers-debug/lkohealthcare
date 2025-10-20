package com.lucknow.healthcare.controller;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.security.JwtUtil;
import com.lucknow.healthcare.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication Controller
 * 
 * Provides REST endpoints for authentication including
 * login, registration, and token management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private com.lucknow.healthcare.service.TwilioService twilioService;
    
    /**
     * Login endpoint
     * 
     * @param loginRequest the login request containing email and password
     * @return ResponseEntity containing JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            
            Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "User not found");
                response.put("message", "Login failed");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("expiresIn", jwtUtil.getExpiration());
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Register endpoint
     * 
     * @param user the user to register
     * @return ResponseEntity containing the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", registeredUser);
            response.put("message", "Registration successful");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("message", "Registration failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Verify email endpoint
     * 
     * @param token the email verification token
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String token) {
        try {
            boolean success = userService.verifyEmail(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Email verified successfully" : "Email verification failed");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("message", "Email verification failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Generate password reset token endpoint
     * 
     * @param email the user's email
     * @return ResponseEntity containing the reset token
     */
    @PostMapping("/reset-password-token")
    public ResponseEntity<Map<String, Object>> generatePasswordResetToken(@RequestParam String email) {
        try {
            String token = userService.generatePasswordResetToken(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Password reset token generated successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("message", "Password reset token generation failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Reset password endpoint
     * 
     * @param token the password reset token
     * @param newPassword the new password
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            boolean success = userService.resetPassword(token, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Password reset successfully" : "Password reset failed");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("message", "Password reset failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get current user endpoint
     * 
     * @param authentication the current authentication
     * @return ResponseEntity containing the current user
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(userOpt.get());
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    /**
     * Logout endpoint
     * 
     * @return ResponseEntity with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Token invalidation is handled client-side by removing the token
        // This endpoint just confirms the logout action
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh token endpoint
     * 
     * @param authentication the current authentication
     * @return ResponseEntity with new token and user data
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "User not found");
                response.put("message", "Token refresh failed");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = userOpt.get();
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
            
            String newToken = jwtUtil.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("user", user);
            response.put("expiresIn", jwtUtil.getExpiration());
            response.put("message", "Token refreshed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Token refresh failed");
            response.put("message", e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
    
    /**
     * Send OTP to phone number
     * 
     * @param request Map containing phone number
     * @return ResponseEntity with success message
     */
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phone");
            
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Phone number is required"
                ));
            }
            
            twilioService.sendOTP(phoneNumber);
            
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully to " + phoneNumber,
                "phone", phoneNumber
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to send OTP: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Verify OTP
     * 
     * @param request Map containing phone number and OTP
     * @return ResponseEntity with verification result
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOTP(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phone");
            String otp = request.get("otp");
            
            if (phoneNumber == null || otp == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "error", "Phone number and OTP are required"
                ));
            }
            
            Map<String, Object> result = twilioService.verifyOTP(phoneNumber, otp);
            
            if ((boolean) result.get("verified")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "verified", false,
                "error", "Verification failed: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Login Request DTO
     */
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
