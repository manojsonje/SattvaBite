package com.sattvabite.order.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class FeignTestConfig {
    
    @Bean
    @Primary
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
