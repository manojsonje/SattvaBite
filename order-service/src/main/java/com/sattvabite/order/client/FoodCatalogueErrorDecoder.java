package com.sattvabite.order.client;

import com.sattvabite.order.exception.ServiceException;
import com.sattvabite.order.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FoodCatalogueErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(FoodCatalogueErrorDecoder.class);
    private final ErrorDecoder.Default defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Error while processing food catalogue service request";
        
        try {
            if (response.body() != null) {
                String responseBody = IOUtils.toString(response.body().asInputStream(), "UTF-8");
                logger.error("Food Catalogue Service error - Status: {}, Response: {}", response.status(), responseBody);
                
                try {
                    // Try to parse the error response as JSON
                    Map<String, Object> errorResponse = objectMapper.readValue(responseBody, HashMap.class);
                    errorMessage = (String) errorResponse.getOrDefault("message", errorMessage);
                } catch (Exception e) {
                    // If parsing fails, use the raw response body as the error message
                    errorMessage = responseBody;
                }
            }
            
            // Create custom exceptions based on the status code
            switch (response.status()) {
                case 400:
                    return new ValidationException("Invalid request to Food Catalogue Service: " + errorMessage);
                case 401:
                    return new ServiceException("Unauthorized access to Food Catalogue Service: " + errorMessage);
                case 403:
                    return new ServiceException("Forbidden access to Food Catalogue Service: " + errorMessage);
                case 404:
                    return new ServiceException("Food item not found in Food Catalogue Service: " + errorMessage);
                case 429:
                    return new ServiceException("Rate limit exceeded for Food Catalogue Service: " + errorMessage);
                case 500:
                case 502:
                case 503:
                case 504:
                    return new ServiceException("Food Catalogue Service is currently unavailable: " + errorMessage);
                default:
                    return defaultDecoder.decode(methodKey, response);
            }
            
        } catch (IOException e) {
            logger.error("Error processing Food Catalogue Service error response", e);
            return new ServiceException("Error processing Food Catalogue Service response: " + e.getMessage(), e);
        }
    }
}
