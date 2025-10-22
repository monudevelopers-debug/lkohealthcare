package com.lucknow.healthcare.security;

import com.lucknow.healthcare.service.interfaces.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JWT Request Filter
 * 
 * Filters incoming requests and validates JWT tokens.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            logger.info("JWT Token found: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
                logger.info("Username extracted from token: " + username);
            } catch (Exception e) {
                logger.error("Unable to get JWT Token or JWT Token has expired: " + e.getMessage());
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String. Header: " + requestTokenHeader);
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Attempting to load user details for: " + username);
            try {
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    logger.info("JWT Token validated successfully for user: " + username);
                    logger.info("User authorities: " + userDetails.getAuthorities());
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info("Authentication set in SecurityContext for user: " + username + " with authorities: " + userDetails.getAuthorities());
                } else {
                    logger.warn("JWT Token validation failed for user: " + username);
                }
            } catch (Exception e) {
                logger.error("Error loading user details for: " + username + ", Error: " + e.getMessage());
            }
        } else {
            logger.warn("Username is null or authentication already exists. Username: " + username + ", Existing auth: " + (SecurityContextHolder.getContext().getAuthentication() != null));
        }
        chain.doFilter(request, response);
    }
}
