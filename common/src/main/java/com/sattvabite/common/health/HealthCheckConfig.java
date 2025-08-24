package com.sattvabite.common.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthCheckConfig {

    @Bean
    public HealthIndicator customHealthIndicator() {
        return () -> Health.up()
                .withDetail("status", "Service is healthy")
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
    }

    @Bean
    public HealthIndicator liveness() {
        return () -> Health.up().withDetail("status", "Liveness check passed").build();
    }

    @Bean
    public HealthIndicator readiness() {
        return () -> {
            // Add any readiness checks here (e.g., database connection, external service availability)
            return Health.up().withDetail("status", "Ready to handle requests").build();
        };
    }
}
