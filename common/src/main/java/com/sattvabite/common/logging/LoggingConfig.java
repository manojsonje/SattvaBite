package com.sattvabite.common.logging;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Configuration
public class LoggingConfig {

    @Bean
    public OncePerRequestFilter mdcLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                try {
                    // Add request ID for correlation
                    String requestId = request.getHeader("X-Request-ID");
                    if (requestId == null || requestId.isBlank()) {
                        requestId = UUID.randomUUID().toString();
                    }
                    
                    MDC.put("requestId", requestId);
                    response.setHeader("X-Request-ID", requestId);
                    
                    // Log request details
                    if (logger.isDebugEnabled()) {
                        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
                        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
                        
                        try {
                            filterChain.doFilter(wrappedRequest, wrappedResponse);
                            
                            // Log response details
                            logger.debug("Request: " + request.getMethod() + " " + 
                                request.getRequestURI() + " - Status: " + response.getStatus());
                                
                            wrappedResponse.copyBodyToResponse();
                        } finally {
                            MDC.clear();
                        }
                    } else {
                        filterChain.doFilter(request, response);
                    }
                } finally {
                    MDC.clear();
                }
            }
        };
    }
}
