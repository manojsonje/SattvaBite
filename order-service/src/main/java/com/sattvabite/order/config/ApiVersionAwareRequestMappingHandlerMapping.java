package com.sattvabite.order.config;

import com.sattvabite.order.annotation.ApiVersion;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom RequestMappingHandlerMapping that supports API versioning.
 * This class extends the default Spring RequestMappingHandlerMapping to add support for
 * the @ApiVersion annotation and custom request mapping conditions.
 */
public class ApiVersionAwareRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return createCondition(method.getDeclaringClass(), method);
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return createCondition(handlerType, null);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo mappingInfo = super.getMappingForMethod(method, handlerType);
        if (mappingInfo == null) {
            return null;
        }

        ApiVersionRequestCondition versionCondition = createCondition(handlerType, method);
        if (versionCondition == null) {
            return mappingInfo;
        }

        return createApiVersionMappingInfo(mappingInfo, versionCondition);
    }

    private ApiVersionRequestCondition createCondition(Class<?> handlerType, Method method) {
        boolean hasApiVersion = (method != null && AnnotatedElementUtils.hasAnnotation(method, ApiVersion.class)) ||
                              (handlerType != null && AnnotatedElementUtils.hasAnnotation(handlerType, ApiVersion.class));

        if (!hasApiVersion) {
            return null;
        }

        return ApiVersionRequestCondition.from(handlerType, method);
    }

    private RequestMappingInfo createApiVersionMappingInfo(RequestMappingInfo mappingInfo, ApiVersionRequestCondition versionCondition) {
        String[] patterns = mappingInfo.getPatternsCondition().getPatterns().stream()
                .map(pattern -> {
                    if (pattern.startsWith("/api/")) {
                        return "/api" + versionCondition.getPathPrefix() + pattern.substring(4);
                    }
                    return versionCondition.getPathPrefix() + pattern;
                })
                .toArray(String[]::new);

        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(patterns)
                .methods(mappingInfo.getMethodsCondition().getMethods().toArray(new org.springframework.web.bind.annotation.RequestMethod[0]))
                .params(mappingInfo.getParamsCondition().getExpressions().stream()
                        .map(expr -> expr.toString())
                        .toArray(String[]::new))
                .headers(mappingInfo.getHeadersCondition().getExpressions().stream()
                        .map(expr -> expr.toString())
                        .toArray(String[]::new))
                .consumes(mappingInfo.getConsumesCondition().getConsumableMediaTypes().stream()
                        .map(mediaType -> mediaType.toString())
                        .toArray(String[]::new))
                .produces(mappingInfo.getProducesCondition().getProducibleMediaTypes().stream()
                        .map(mediaType -> mediaType.toString())
                        .toArray(String[]::new))
                .mappingName(mappingInfo.getName());

        // Add custom conditions
        RequestCondition<?> customCondition = mappingInfo.getCustomCondition();
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }

        // Add the API version condition
        builder.customCondition(versionCondition);

        return builder.build();
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        // Skip registration of handler methods that don't have the @ApiVersion annotation
        // when the class is annotated with @ApiVersion
        if (mapping == null) {
            return;
        }

        super.registerHandlerMethod(handler, method, mapping);
    }
}
