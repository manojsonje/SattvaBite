package com.sattvabite.order.config;

import com.sattvabite.order.service.OrderService;
import com.sattvabite.order.service.client.UserServiceClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class OrderControllerTestConfig {
    
    @MockBean
    private UserServiceClient userServiceClient;
    
    @MockBean
    private OrderService orderService;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
