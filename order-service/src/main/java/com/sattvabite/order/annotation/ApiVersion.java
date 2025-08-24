package com.sattvabite.order.annotation;

import com.sattvabite.order.config.ApiVersioningConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the API version for a controller or method.
 * The version can be specified as a string (e.g., "1.0", "2.0").
 * 
 * Example usage:
 * 
 * @RestController
 * @RequestMapping("/api/orders")
 * @ApiVersion("1.0")
 * public class OrderController { ... }
 * 
 * or for method-level versioning:
 * 
 * @GetMapping("/{id}")
 * @ApiVersion({"1.0", "2.0"}) // Supports both versions
 * public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) { ... }
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    
    /**
     * The API version(s) that this controller or method supports.
     * Defaults to "1.0".
     */
    String[] value() default ApiVersioningConfig.VERSION_1;
    
    /**
     * Whether this version is deprecated.
     * If true, a deprecation warning will be included in the API documentation.
     */
    boolean deprecated() default false;
    
    /**
     * The date when this API version will be removed (ISO 8601 format).
     * Example: "2024-12-31"
     */
    String removalDate() default "";
    
    /**
     * A message explaining why this version is deprecated and what to use instead.
     */
    String deprecatedMessage() default "";
}
