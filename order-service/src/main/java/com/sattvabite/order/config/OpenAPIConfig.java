package com.sattvabite.order.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Value("${server.servlet.context-path:/api/orders}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9094" + contextPath)
                                .description("Local Development Server")
                ))
                .info(new Info()
                        .title(applicationName + " API Documentation")
                        .version(applicationVersion)
                        .description("""
                                ## Order Service API
                                
                                This service is responsible for managing orders in the SattvaBite food delivery platform.
                                It handles order creation, retrieval, updates, and status management.
                                
                                ### Authentication
                                Most endpoints require a valid JWT token for authentication. 
                                Include the token in the `Authorization` header as a Bearer token.
                                
                                ### Error Handling
                                The API uses standard HTTP status codes to indicate success or failure:
                                - 2xx: Success
                                - 4xx: Client errors (invalid requests, unauthorized, etc.)
                                - 5xx: Server errors
                                
                                ### Rate Limiting
                                The API is rate limited. Check response headers for rate limit information:
                                - `X-RateLimit-Limit`: Maximum number of requests allowed in the time window
                                - `X-RateLimit-Remaining`: Remaining number of requests in the current window
                                - `X-RateLimit-Reset`: Time (in seconds) until the rate limit resets
                                """)
                        .contact(new Contact()
                                .name("SattvaBite Support")
                                .email("support@sattvabite.com")
                                .url("https://support.sattvabite.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}
