package com.sattvabite.apigateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        return createFallbackResponse("Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/order-fallback")
    public Mono<ResponseEntity<Map<String, Object>>> orderServiceFallback() {
        return createFallbackResponse("Order Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/food-fallback")
    public Mono<ResponseEntity<Map<String, Object>>> foodServiceFallback() {
        return createFallbackResponse("Food Catalogue Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/restaurant-fallback")
    public Mono<ResponseEntity<Map<String, Object>>> restaurantServiceFallback() {
        return createFallbackResponse("Restaurant Service is currently unavailable. Please try again later.");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", message);
        response.put("success", false);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
