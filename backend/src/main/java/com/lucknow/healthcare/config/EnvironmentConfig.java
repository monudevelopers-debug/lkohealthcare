package com.lucknow.healthcare.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Environment Configuration
 * 
 * Loads environment variables from .env file for development.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Configuration
public class EnvironmentConfig {
    
    @Bean
    @Primary
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }
}
