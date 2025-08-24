package com.sattvabite.order.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sattvabite.order.service.client.UserServiceClient;
import com.sattvabite.order.service.OrderService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@ActiveProfiles("test")
@Import({
    // Add any additional configuration classes needed for testing
})
public class TestConfig {
    
    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        return new WireMockServer(0); // Use random port
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    // Mock external service clients
    @MockBean
    private UserServiceClient userServiceClient;
    
    // Mock any other beans that might cause context loading issues
    @MockBean
    private OrderService orderService;
}
