package com.sattvabite.order.exception;

import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when request validation fails.
 */
@Getter
public class ValidationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final List<ValidationError> errors = new ArrayList<>();
    private final String errorCode;

    public ValidationException(String message) {
        super(message);
        this.errorCode = null;
    }
    
    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    
    public ValidationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ValidationException addError(String field, String message) {
        this.errors.add(new ValidationError(field, message));
        return this;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;
        
        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
