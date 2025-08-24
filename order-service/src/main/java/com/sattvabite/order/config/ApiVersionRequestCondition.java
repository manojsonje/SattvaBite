package com.sattvabite.order.config;

import com.sattvabite.order.annotation.ApiVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom request condition to handle API versioning.
 * This class is responsible for matching incoming requests to the appropriate
 * controller method based on the API version specified in the URL, request parameter, or header.
 */
public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private static final Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)\\.?(\\d*)");
    private final Set<String> versions;
    private final String pathPrefix;

    public ApiVersionRequestCondition(String... versions) {
        this.versions = new HashSet<>(Arrays.asList(versions));
        this.pathPrefix = createPathPrefix();
    }

    private String createPathPrefix() {
        if (versions.isEmpty()) {
            return "";
        }
        // Use the first version for path-based versioning
        String version = versions.iterator().next();
        return "/v" + version.replace(".", "") + "/";
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        Set<String> allVersions = new HashSet<>(this.versions);
        allVersions.addAll(other.versions);
        return new ApiVersionRequestCondition(allVersions.toArray(new String[0]));
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        String requestVersion = getRequestedVersion(request);
        
        // If no version is specified in the request, match the default version (1.0)
        if (requestVersion == null) {
            requestVersion = ApiVersioningConfig.VERSION_1;
        }

        // Check if any of the versions in this condition match the requested version
        for (String version : versions) {
            if (versionMatches(version, requestVersion)) {
                return this;
            }
        }

        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        // Prefer the condition with the highest version number
        String maxThis = versions.stream().max(Comparator.naturalOrder()).orElse("");
        String maxOther = other.versions.stream().max(Comparator.naturalOrder()).orElse("");
        return maxOther.compareTo(maxThis);
    }

    private boolean versionMatches(String version, String requestVersion) {
        if (version.equals(requestVersion)) {
            return true;
        }
        
        // Support for major version matching (e.g., 1.0 matches 1.1 if only major version is specified)
        if (version.matches("\\d+\\.\\d+") && requestVersion.matches("\\d+\\.\\d+")) {
            String[] versionParts = version.split("\\.");
            String[] requestParts = requestVersion.split("\\.");
            
            if (versionParts.length >= 1 && requestParts.length >= 1 && 
                versionParts[0].equals(requestParts[0])) {
                return true;
            }
        }
        
        return false;
    }

    private String getRequestedVersion(HttpServletRequest request) {
        // 1. Check URL path (e.g., /api/v1/orders)
        String pathInfo = request.getRequestURI();
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(pathInfo);
        if (matcher.find()) {
            return matcher.group(1) + "." + (matcher.group(2).isEmpty() ? "0" : matcher.group(2));
        }

        // 2. Check request parameter (e.g., /api/orders?api-version=1.0)
        String paramVersion = request.getParameter(ApiVersioningConfig.API_VERSION_PARAM);
        if (paramVersion != null && !paramVersion.trim().isEmpty()) {
            return normalizeVersion(paramVersion);
        }

        // 3. Check custom header (e.g., X-API-Version: 1.0)
        String headerVersion = request.getHeader(ApiVersioningConfig.API_VERSION_HEADER);
        if (headerVersion != null && !headerVersion.trim().isEmpty()) {
            return normalizeVersion(headerVersion);
        }

        return null;
    }

    private String normalizeVersion(String version) {
        // Remove 'v' prefix if present
        version = version.startsWith("v") ? version.substring(1) : version;
        
        // Ensure version has at least major and minor parts
        String[] parts = version.split("\\.");
        if (parts.length == 1) {
            return parts[0] + ".0";
        }
        return version;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public Set<String> getVersions() {
        return Collections.unmodifiableSet(versions);
    }

    /**
     * Factory method to create an ApiVersionRequestCondition from a method or class annotation.
     */
    public static ApiVersionRequestCondition from(Class<?> handlerType, Method method) {
        // Check method-level annotation first
        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        if (methodAnnotation != null) {
            return new ApiVersionRequestCondition(methodAnnotation.value());
        }

        // Then check class-level annotation
        ApiVersion classAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        if (classAnnotation != null) {
            return new ApiVersionRequestCondition(classAnnotation.value());
        }

        // Default to version 1.0 if no annotation is present
        return new ApiVersionRequestCondition(ApiVersioningConfig.VERSION_1);
    }
}
