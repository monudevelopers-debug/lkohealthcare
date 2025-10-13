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
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
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
                return ResponseEntity.badRequest().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userOpt.get());
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            response.put("message", "Login failed");
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
