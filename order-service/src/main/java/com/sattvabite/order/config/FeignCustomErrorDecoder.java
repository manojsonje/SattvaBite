package com.sattvabite.order.config;

import com.sattvabite.order.exception.FeignClientException;
import com.sattvabite.order.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * Custom error decoder for Feign client to handle different HTTP error responses.
 */
public class FeignCustomErrorDecoder implements ErrorDecoder {
    private static final Logger log = LoggerFactory.getLogger(FeignCustomErrorDecoder.class);

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign client error - Status: {}, Method: {}", response.status(), methodKey);
        
        switch (response.status()) {
            case 400:
                return new FeignClientException("Bad Request - " + response.reason(),
                        HttpStatus.BAD_REQUEST);
            case 401:
                return new FeignClientException("Unauthorized - " + response.reason(),
                        HttpStatus.UNAUTHORIZED);
            case 403:
                return new FeignClientException("Forbidden - " + response.reason(),
                        HttpStatus.FORBIDDEN);
            case 404:
                return new ResourceNotFoundException("Resource not found - " + methodKey);
            case 429:
                return new FeignClientException("Too Many Requests - " + response.reason(),
                        HttpStatus.TOO_MANY_REQUESTS);
            case 500:
                return new FeignClientException("Internal Server Error - " + response.reason(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            case 502:
            case 503:
            case 504:
                return new FeignClientException("Service Unavailable - " + response.reason(),
                        HttpStatus.SERVICE_UNAVAILABLE);
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
