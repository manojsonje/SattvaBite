package com.sattvabite.order.config;

import feign.*;
import feign.codec.ErrorDecoder;
import feign.optionals.OptionalDecoder;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.LoggerFactory;

@Configuration
public class FeignConfig {

    // Timeout configurations
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 10000;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Propagate headers like Authorization, etc.
            ServletRequestAttributes attributes = (ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authorization = attributes.getRequest().getHeader("Authorization");
                if (authorization != null) {
                    requestTemplate.header("Authorization", authorization);
                }
                
                // Add correlation ID for distributed tracing
                String correlationId = attributes.getRequest().getHeader("X-Correlation-ID");
                if (correlationId != null) {
                    requestTemplate.header("X-Correlation-ID", correlationId);
                }
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public FeignClientProperties feignClientProperties() {
        return new FeignClientProperties();
    }

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(30000))
                .slidingWindowSize(10)
                .permittedNumberOfCallsInHalfOpenState(5)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .build();
    }

    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
                .intervalFunction(interval -> 1000L)
                .retryExceptions(FeignException.class, IOException.class, TimeoutException.class)
                .build();
    }

    @Bean
    public RateLimiterConfig rateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(100))
                .build();
    }

    // Using Resilience4j's retry mechanism instead of Feign's retryer
    // The retry configuration is now handled by Resilience4j's RetryRegistry

    @Bean
    public feign.okhttp.OkHttpClient client() {
        okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
            .connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MS))
            .readTimeout(Duration.ofMillis(READ_TIMEOUT_MS))
            .retryOnConnectionFailure(true)
            .connectionPool(new ConnectionPool(100, 5, TimeUnit.MINUTES))
            .build();
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }

    @Bean
    @Scope("prototype")
    public <T> Feign.Builder feignBuilder(
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry,
            RateLimiterRegistry rateLimiterRegistry,
            ObjectFactory<HttpMessageConverters> messageConverters) {
        
        // Get or create the circuit breaker, retry, and rate limiter instances
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("feignClient", circuitBreakerConfig());
        Retry retry = retryRegistry.retry("feignClient", retryConfig());
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("feignClient", rateLimiterConfig());
        
        // Create the decorators with resilience patterns
        FeignDecorators decorators = FeignDecorators.builder()
                .withCircuitBreaker(circuitBreaker)
                .withRetry(retry)
                .withRateLimiter(rateLimiter)
                .withFallbackFactory(throwable -> {
                    // Log the error for debugging
                    LoggerFactory.getLogger(FeignConfig.class)
                        .warn("Fallback triggered for Feign client: {}", throwable.getMessage());
                    
                    // Return a default fallback that returns null for all methods
                    return (T) Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] { Object.class },
                        (proxy, method, args) -> {
                            // Log the fallback method call
                            LoggerFactory.getLogger(FeignConfig.class)
                                .debug("Fallback method called: {}", method.getName());
                            return null;
                        });
                })
                .build();
        
        // Build the Feign client with all configurations
        return Resilience4jFeign.builder(decorators)
                .client(client())
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.BASIC) // Reduced from FULL to BASIC for less verbose logging
                .encoder(new SpringEncoder(messageConverters))
                .decoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters))))
                .retryer(Retryer.NEVER_RETRY) // Using Resilience4j's retry instead of Feign's
                .options(new Request.Options(CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS, 
                                           READ_TIMEOUT_MS, TimeUnit.MILLISECONDS, true))
                .contract(new SpringMvcContract())
                .requestInterceptor(requestInterceptor());
    }

    @Bean
    public HttpMessageConverters messageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(converter);
        return new HttpMessageConverters(converters);
    }
}
