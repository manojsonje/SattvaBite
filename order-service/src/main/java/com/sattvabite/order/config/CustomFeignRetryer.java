package com.sattvabite.order.config;

import feign.RetryableException;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * Custom retryer for Feign clients that provides more control over retry logic.
 * Implements exponential backoff and configurable retry policies.
 */
public class CustomFeignRetryer implements Retryer {
    private static final Logger log = LoggerFactory.getLogger(CustomFeignRetryer.class);

    private final long period;
    private final long maxPeriod;
    private final int maxAttempts;
    private int attempt;
    private long sleptForMillis;

    /**
     * Default constructor with default values.
     */
    public CustomFeignRetryer() {
        this(1000, 3);
    }

    /**
     * Constructor with custom values.
     *
     * @param periodMs    initial sleep period in milliseconds
     * @param maxAttempts maximum number of attempts
     */
    public CustomFeignRetryer(long periodMs, int maxAttempts) {
        this.period = periodMs;
        this.maxPeriod = periodMs * 10; // Max period is 10x the initial period
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
        this.sleptForMillis = 0L;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (e == null) {
            throw new NullPointerException("RetryableException cannot be null");
        }
        
        if (attempt >= maxAttempts) {
            log.error("Max retries ({}) reached. Giving up. Error: {}", maxAttempts, e.getMessage());
            throw e;
        }

        long sleepTime = calculateSleepTime(attempt);
        
        try {
            log.warn("Retry attempt {} of {} after {} ms. Error: {}", 
                    attempt, maxAttempts, sleepTime, e.getMessage());
            
            Thread.sleep(sleepTime);
            sleptForMillis += sleepTime;
            attempt++;
            
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Retry interrupted", ex);
            throw e;
        }
    }
    
    /**
     * Calculate sleep time with exponential backoff and jitter
     */
    private long calculateSleepTime(int attempt) {
        // Exponential backoff with jitter: sleep = min(period * 2^attempt + random_jitter, maxPeriod)
        long sleepTime = (long) Math.min(period * Math.pow(2, attempt - 1), maxPeriod);
        
        // Add jitter (up to 10% of sleep time)
        long jitter = (long) (Math.random() * sleepTime * 0.1);
        sleepTime += jitter;
        
        return sleepTime;
    }
    
    @Override
    public Retryer clone() {
        return new CustomFeignRetryer(period, maxAttempts);
    }
    
    /**
     * Determines if an exception should trigger a retry.
     *
     * @param e the exception to check
     * @return true if the exception should trigger a retry, false otherwise
     */
    protected boolean shouldRetry(Throwable e) {
        // Retry on network-related exceptions
        return e instanceof ConnectException ||
               e instanceof SocketTimeoutException ||
               e instanceof SocketException ||
               e instanceof HttpTimeoutException ||
               e instanceof java.io.IOException;
    }
}
