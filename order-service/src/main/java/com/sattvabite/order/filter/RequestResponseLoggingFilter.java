package com.sattvabite.order.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log request details
        if (logger.isDebugEnabled()) {
            logRequestDetails(httpRequest);
        }

        // Wrap request and response to read multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Log response details
            if (logger.isDebugEnabled()) {
                logResponseDetails(httpRequest, wrappedResponse);
            }
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequestDetails(HttpServletRequest request) throws IOException {
        StringBuilder requestLog = new StringBuilder();
        requestLog.append("\n=== Request Details ===\n");
        requestLog.append("Method: ").append(request.getMethod()).append("\n");
        requestLog.append("URI: ").append(request.getRequestURI()).append("\n");
        requestLog.append("Query String: ").append(request.getQueryString()).append("\n");
        
        // Log headers
        requestLog.append("Headers: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestLog.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }
        
        // Log request body if it's a POST, PUT, or PATCH request
        String method = request.getMethod().toUpperCase();
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            byte[] buf = wrappedRequest.getContentAsByteArray();
            if (buf.length > 0) {
                String requestBody = new String(buf, StandardCharsets.UTF_8);
                requestLog.append("Request Body: ").append(requestBody).append("\n");
            }
        }
        
        logger.debug(requestLog.toString());
    }

    private void logResponseDetails(HttpServletRequest request, ContentCachingResponseWrapper wrappedResponse) throws IOException {
        StringBuilder responseLog = new StringBuilder();
        responseLog.append("\n=== Response Details ===\n");
        responseLog.append("Status: ").append(wrappedResponse.getStatus()).append("\n");
        
        // Log response headers
        responseLog.append("Headers: \n");
        wrappedResponse.getHeaderNames().forEach(headerName -> 
            wrappedResponse.getHeaders(headerName).forEach(headerValue ->
                responseLog.append("  ").append(headerName).append(": ").append(headerValue).append("\n")
            )
        );
        
        // Log response body
        byte[] buf = wrappedResponse.getContentAsByteArray();
        if (buf.length > 0) {
            String responseBody = new String(buf, StandardCharsets.UTF_8);
            responseLog.append("Response Body: ").append(responseBody).append("\n");
        }
        
        logger.debug(responseLog.toString());
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
