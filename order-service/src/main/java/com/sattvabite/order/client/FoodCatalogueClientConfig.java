package com.sattvabite.order.client;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FoodCatalogueClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FoodCatalogueErrorDecoder();
    }
}
