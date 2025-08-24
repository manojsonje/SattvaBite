package com.sattvabite.common.logging;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class LoggingUtils {
    
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    
    private LoggingUtils() {
        // Private constructor to prevent instantiation
    }
    
    public static String getRequestId() {
        return MDC.get("requestId");
    }
    
    public static String getRequestIdHeader() {
        return REQUEST_ID_HEADER;
    }
    
    public static void logRequest(Logger logger, HttpServletRequest request, Object body) {
        if (logger.isDebugEnabled()) {
            String requestId = getRequestId();
            String headers = Collections.list(request.getHeaderNames())
                .stream()
                .map(headerName -> headerName + ": " + request.getHeader(headerName))
                .collect(Collectors.joining(", "));
            
            logger.debug("Request [{}] - Method: {}, URI: {}, Headers: {}, Body: {}", 
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                headers,
                body != null ? body.toString() : "");
        }
    }
    
    public static void logResponse(Logger logger, int status, Object body) {
        if (logger.isDebugEnabled()) {
            String requestId = getRequestId();
            String statusText = HttpStatus.valueOf(status).getReasonPhrase();
            
            logger.debug("Response [{}] - Status: {} ({}), Body: {}", 
                requestId,
                status,
                statusText,
                body != null ? body.toString() : "");
        }
    }
    
    public static void logError(Logger logger, String message, Throwable throwable) {
        String requestId = getRequestId();
        logger.error("[{}] {}", requestId, message, throwable);
    }
    
    public static void logInfo(Logger logger, String message, Object... args) {
        String requestId = getRequestId();
        logger.info("[{}] {}", requestId, String.format(message, args));
    }
    
    public static void logDebug(Logger logger, String message, Object... args) {
        if (logger.isDebugEnabled()) {
            String requestId = getRequestId();
            logger.debug("[{}] {}", requestId, String.format(message, args));
        }
    }
    
    public static String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] buf = response.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, 0, buf.length, response.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            return "[unknown] (error reading response body: " + e.getMessage() + ")";
        }
        return "";
    }
    
    public static String getRequestBody(ContentCachingRequestWrapper request) {
        try {
            byte[] buf = request.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, 0, buf.length, request.getCharacterEncoding());
            }
        } catch (UnsupportedEncodingException e) {
            return "[unknown] (error reading request body: " + e.getMessage() + ")";
        }
        return "";
    }
}
