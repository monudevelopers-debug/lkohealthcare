package com.lucknow.healthcare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * Database Configuration
 * 
 * Configures database connection, JPA settings, and transaction management.
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.lucknow.healthcare.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
