package com.sattvabite.order.client;

import com.sattvabite.order.dto.FoodItemDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "food-catalog-service", 
             url = "${feign.client.config.food-catalog-service.url}",
             configuration = FoodCatalogueClientConfig.class)
public interface FoodCatalogueClient {

    @GetMapping("/api/foods/{id}")
    @Retry(name = "food-catalog-service")
    @CircuitBreaker(name = "food-catalog-service", fallbackMethod = "getFoodItemByIdFallback")
    ResponseEntity<FoodItemDTO> getFoodItemById(@PathVariable("id") Long id);
    
    @PostMapping("/api/foods/ids")
    @Retry(name = "food-catalog-service")
    @CircuitBreaker(name = "food-catalog-service", fallbackMethod = "getFoodItemsByIdsFallback")
    ResponseEntity<List<FoodItemDTO>> getFoodItemsByIds(@RequestBody List<Long> ids);
    
    // Fallback methods
    default ResponseEntity<FoodItemDTO> getFoodItemByIdFallback(Long id, Throwable t) {
        // Log the error or take appropriate action
        return ResponseEntity.status(503).build();
    }
    
    default ResponseEntity<List<FoodItemDTO>> getFoodItemsByIdsFallback(List<Long> ids, Throwable t) {
        // Log the error or take appropriate action
        return ResponseEntity.status(503).build();
    }
}
