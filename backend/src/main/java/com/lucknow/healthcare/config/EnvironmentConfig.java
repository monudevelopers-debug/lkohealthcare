package com.lucknow.healthcare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Environment Configuration
 * 
 * Loads environment variables from .env file for development.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Configuration
@PropertySource(value = "file:./.env", ignoreResourceNotFound = true)
public class EnvironmentConfig {
    // This configuration class loads the .env file as a property source
}
