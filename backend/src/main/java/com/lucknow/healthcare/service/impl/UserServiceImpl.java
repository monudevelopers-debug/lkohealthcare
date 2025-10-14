package com.lucknow.healthcare.service.impl;

import com.lucknow.healthcare.entity.User;
import com.lucknow.healthcare.enums.UserRole;
import com.lucknow.healthcare.enums.UserStatus;
import com.lucknow.healthcare.repository.UserRepository;
import com.lucknow.healthcare.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for User entity operations
 * 
 * Implements business logic for user management including
 * authentication, registration, profile management, and admin operations.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(User user) {
        // Check if email already exists
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setEmailVerificationToken(generateToken());
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
    }
    
    @Override
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found with ID: " + user.getId());
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public boolean updatePassword(UUID userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        
        userRepository.save(user);
        return true;
    }
    
    @Override
    public User updateUserStatus(UUID userId, UserStatus status) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        User user = userOpt.get();
        user.setStatus(status);
        
        return userRepository.save(user);
    }
    
    @Override
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email verification token");
        }
        
        User user = userOpt.get();
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        
        userRepository.save(user);
        return true;
    }
    
    @Override
    public String generatePasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        
        User user = userOpt.get();
        String token = generateToken();
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(24)); // Token expires in 24 hours
        
        userRepository.save(user);
        return token;
    }
    
    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByPasswordResetToken(token);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid password reset token");
        }
        
        User user = userOpt.get();
        
        if (!user.isPasswordResetTokenValid()) {
            throw new IllegalArgumentException("Password reset token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        
        userRepository.save(user);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findActiveUsersByRole(UserRole role) {
        return userRepository.findActiveUsersByRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUsersByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }
    
    @Override
    public boolean deleteUser(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        User user = userOpt.get();
        user.setStatus(UserStatus.INACTIVE);
        
        userRepository.save(user);
        return true;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(username);
        
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        User user = userOpt.get();
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
                return authorities;
            }
            
            @Override
            public String getPassword() {
                return user.getPassword();
            }
            
            @Override
            public String getUsername() {
                return user.getEmail();
            }
            
            @Override
            public boolean isAccountNonExpired() {
                return true;
            }
            
            @Override
            public boolean isAccountNonLocked() {
                return user.getStatus() != UserStatus.SUSPENDED;
            }
            
            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }
            
            @Override
            public boolean isEnabled() {
                return user.getStatus() == UserStatus.ACTIVE;
            }
        };
    }
    
    /**
     * Generate a random token for email verification or password reset
     * 
     * @return a random token string
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAllUsers() {
        return userRepository.count();
    }
}
