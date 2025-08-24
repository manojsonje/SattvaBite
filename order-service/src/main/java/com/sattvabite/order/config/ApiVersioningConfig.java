package com.sattvabite.order.config;

import com.sattvabite.order.annotation.ApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {

    public static final String API_VERSION_HEADER = "X-API-Version";
    public static final String API_VERSION_PARAM = "api-version";
    
    public static final String VERSION_1 = "1.0";
    public static final String VERSION_2 = "2.0";
    
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new ApiVersionAwareRequestMappingHandlerMapping();
    }
    
    /**
     * Custom request mapping handler that supports API versioning via:
     * 1. URL path (e.g., /api/v1/orders, /api/v2/orders)
     * 2. Request parameter (e.g., /api/orders?api-version=1.0)
     * 3. Custom header (e.g., X-API-Version: 1.0)
     */
    public static class ApiVersionAwareRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        
        public ApiVersionAwareRequestMappingHandlerMapping() {
            // Set order to ensure this runs before the default handler mapping
            setOrder(0);
        }
        
        @Override
        protected boolean isHandler(Class<?> beanType) {
            // Only consider controllers with @ApiVersion annotation
            return super.isHandler(beanType) && 
              (beanType.isAnnotationPresent(ApiVersion.class) || 
               beanType.getPackage().getName().startsWith("com.sattvabite.order.controller"));
        }
    }
}
