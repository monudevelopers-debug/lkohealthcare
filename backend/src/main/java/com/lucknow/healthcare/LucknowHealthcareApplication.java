package com.lucknow.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Lucknow Nursing Healthcare Services MVP
 * 
 * This is the entry point for the Spring Boot application that provides
 * healthcare services including nursing, elderly care, physiotherapy,
 * child care, and ambulance services for the Lucknow region.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class LucknowHealthcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(LucknowHealthcareApplication.class, args);
    }
}
