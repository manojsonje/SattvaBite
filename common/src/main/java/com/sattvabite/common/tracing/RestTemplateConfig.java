package com.sattvabite.common.tracing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    private final TracingRequestInterceptor tracingRequestInterceptor;

    @Autowired
    public RestTemplateConfig(TracingRequestInterceptor tracingRequestInterceptor) {
        this.tracingRequestInterceptor = tracingRequestInterceptor;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(tracingRequestInterceptor));
        return restTemplate;
    }
}
