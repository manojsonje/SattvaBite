package com.sattvabite.order.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for Feign client related errors.
 */
public class FeignClientException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructs a new FeignClientException with the specified detail message and status.
     *
     * @param message the detail message
     * @param status  the HTTP status code
     */
    public FeignClientException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return the HTTP status code
     */
    public HttpStatus getStatus() {
        return status;
    }
}
