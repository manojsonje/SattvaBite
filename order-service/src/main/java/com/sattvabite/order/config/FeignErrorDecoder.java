package com.sattvabite.order.config;

import com.sattvabite.order.exception.ServiceException;
import com.sattvabite.order.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FeignErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(FeignErrorDecoder.class);
    private final ErrorDecoder.Default defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Error while processing request";
        
        try {
            if (response.body() != null) {
                String responseBody = IOUtils.toString(response.body().asInputStream(), "UTF-8");
                logger.error("Feign client error - Status: {}, Response: {}", response.status(), responseBody);
                
                try {
                    // Try to parse the error response as JSON
                    Map<String, Object> errorResponse = objectMapper.readValue(responseBody, HashMap.class);
                    errorMessage = (String) errorResponse.getOrDefault("message", errorMessage);
                } catch (Exception e) {
                    // If parsing fails, use the raw response body as the error message
                    errorMessage = responseBody;
                }
            }
            
            // Handle different HTTP status codes
            return switch (response.status()) {
                case 400 -> new ValidationException("Bad Request: " + errorMessage);
                case 401 -> new ServiceException("Unauthorized: " + errorMessage);
                case 403 -> new ServiceException("Forbidden: " + errorMessage);
                case 404 -> new ServiceException("Resource not found: " + errorMessage);
                case 408 -> new ServiceException("Request Timeout: " + errorMessage);
                case 429 -> new ServiceException("Too Many Requests: " + errorMessage);
                case 500 -> new ServiceException("Internal Server Error: " + errorMessage);
                case 502 -> new ServiceException("Bad Gateway: " + errorMessage);
                case 503 -> new ServiceException("Service Unavailable: " + errorMessage);
                case 504 -> new ServiceException("Gateway Timeout: " + errorMessage);
                default -> defaultDecoder.decode(methodKey, response);
            };
            
        } catch (IOException e) {
            logger.error("Error processing Feign error response", e);
            return new ServiceException("Error processing service response: " + e.getMessage(), e);
        }
    }
}
