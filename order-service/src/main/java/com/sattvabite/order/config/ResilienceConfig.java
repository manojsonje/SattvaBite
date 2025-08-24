package com.sattvabite.order.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // Percentage of failures to start opening the circuit
                .waitDurationInOpenState(Duration.ofMillis(10000)) // Time to wait before changing from OPEN to HALF_OPEN
                .permittedNumberOfCallsInHalfOpenState(3) // Number of permitted calls in HALF_OPEN state
                .slidingWindowSize(10) // Number of calls to calculate failure rate
                .minimumNumberOfCalls(5) // Minimum number of calls to calculate failure rate
                .recordExceptions(Exception.class) // Exceptions to record as failures
                .build();

        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3) // Maximum number of retry attempts
                .waitDuration(Duration.ofMillis(1000)) // Wait time between retries
                .retryExceptions(Exception.class) // Exceptions that should trigger retry
                .build();

        return RetryRegistry.of(retryConfig);
    }

    // Example of a custom circuit breaker configuration for a specific service
    @Bean(name = "foodServiceCircuitBreaker")
    public CircuitBreakerConfig foodServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(40)
                .waitDurationInOpenState(Duration.ofMillis(5000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(5)
                .minimumNumberOfCalls(3)
                .recordExceptions(Exception.class)
                .build();
    }

    // Example of a custom retry configuration for a specific service
    @Bean(name = "restaurantServiceRetryConfig")
    public RetryConfig restaurantServiceRetryConfig() {
        return RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(Exception.class)
                .build();
    }
}
