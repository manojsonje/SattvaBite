package com.sattvabite.order.exception;

import java.io.Serial;

/**
 * Exception thrown when an error occurs during business logic execution.
 */
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final String errorCode;
    private final String details;

    public ServiceException(String message) {
        super(message);
        this.errorCode = null;
        this.details = null;
    }
    
    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }
    
    public ServiceException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.details = null;
    }
    
    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getDetails() {
        return details;
    }
}
