package com.lucknow.healthcare.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Access Denied Handler
 * 
 * Handles authorization failures and returns appropriate HTTP responses.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"" + accessDeniedException.getMessage() + "\"}");
    }
}

