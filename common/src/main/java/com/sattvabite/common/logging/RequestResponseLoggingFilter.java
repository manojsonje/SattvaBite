package com.sattvabite.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Generate or use existing request ID
        String requestId = request.getHeader(LoggingUtils.getRequestIdHeader());
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Set request ID in MDC and response headers
        MDC.put("requestId", requestId);
        response.setHeader(LoggingUtils.getRequestIdHeader(), requestId);
        
        // Wrap request and response to enable multiple reads of the body
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            // Log request
            if (logger.isDebugEnabled()) {
                String requestBody = LoggingUtils.getRequestBody(wrappedRequest);
                LoggingUtils.logRequest(logger, wrappedRequest, requestBody);
            }
            
            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
            // Log response
            if (logger.isDebugEnabled()) {
                String responseBody = LoggingUtils.getResponseBody(wrappedResponse);
                LoggingUtils.logResponse(logger, wrappedResponse.getStatus(), responseBody);
            }
            
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error processing request", e);
            throw e;
        } finally {
            MDC.clear();
            // Ensure the response is written back to the client
            wrappedResponse.copyBodyToResponse();
        }
    }
}
