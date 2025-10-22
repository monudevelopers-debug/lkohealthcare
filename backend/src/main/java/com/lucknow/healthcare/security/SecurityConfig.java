package com.lucknow.healthcare.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration
 * 
 * Configures Spring Security with JWT authentication and CORS support.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**", "/actuator/**", "/health/**").permitAll()
                .requestMatchers("/users/register", "/users/authenticate", "/users/verify-email", 
                               "/users/reset-password-token", "/users/reset-password",
                               "/users/email-exists/**").permitAll()
                .requestMatchers("/service-categories/active", "/services/active", "/services",
                               "/providers/available-verified", "/providers/top-rated",
                               "/providers/available", "/providers/search", "/providers/search/qualification")
                .permitAll()
                // Payment gateway callbacks must be public (no authentication)
                .requestMatchers("/payments/paytm/callback", "/payments/razorpay/callback", 
                               "/payments/stripe/webhook").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/**").hasAnyRole("ADMIN", "USER", "PROVIDER", "CUSTOMER")
                .requestMatchers("/service-categories/**").hasAnyRole("ADMIN", "PROVIDER")
                .requestMatchers("/services/**").hasAnyRole("ADMIN", "USER", "PROVIDER", "CUSTOMER")
                .requestMatchers("/service-requests/**").hasAnyRole("ADMIN", "PROVIDER")
                .requestMatchers("/providers/available-verified", "/providers/top-rated", 
                               "/providers/available", "/providers/search", "/providers/search/qualification").permitAll()
                .requestMatchers("/providers/**").hasAnyRole("ADMIN", "PROVIDER")
                .requestMatchers("/bookings/**").hasAnyRole("ADMIN", "USER", "PROVIDER", "CUSTOMER")
                .requestMatchers("/booking-rejections/**").hasAnyRole("ADMIN", "PROVIDER")
                .requestMatchers("/patients/**").hasAnyRole("ADMIN", "USER", "CUSTOMER")
                .requestMatchers("/consents/**").hasAnyRole("ADMIN", "USER", "CUSTOMER")
                .requestMatchers("/payments/**").hasAnyRole("ADMIN", "USER", "PROVIDER", "CUSTOMER")
                .requestMatchers("/reviews/**").hasAnyRole("ADMIN", "USER", "PROVIDER", "CUSTOMER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
