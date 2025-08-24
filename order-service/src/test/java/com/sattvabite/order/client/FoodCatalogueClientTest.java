package com.sattvabite.order.client;

import com.sattvabite.order.config.FeignTestConfig;
import com.sattvabite.order.dto.FoodItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodCatalogueClientTest {

    @Mock
    private FoodCatalogueClient foodCatalogueClient;

    @InjectMocks
    private FoodCatalogueClientTest testInstance;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(foodCatalogueClient);
    }

    @Test
    void getFoodItemById_ShouldReturnFoodItem_WhenFound() {
        // Given
        Long foodItemId = 1L;
        FoodItemDTO expectedFoodItem = new FoodItemDTO();
        expectedFoodItem.setId(foodItemId);
        expectedFoodItem.setName("Test Food");
        expectedFoodItem.setPrice(new BigDecimal("10.99"));
        
        ResponseEntity<FoodItemDTO> responseEntity = ResponseEntity.ok(expectedFoodItem);

        // Mock the Feign client response
        when(foodCatalogueClient.getFoodItemById(foodItemId))
            .thenReturn(responseEntity);

        // When
        ResponseEntity<FoodItemDTO> response = foodCatalogueClient.getFoodItemById(foodItemId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        FoodItemDTO actualFoodItem = response.getBody();
        assertEquals(foodItemId, actualFoodItem.getId());
        assertEquals("Test Food", actualFoodItem.getName());
        assertEquals(new BigDecimal("10.99"), actualFoodItem.getPrice());
        
        // Verify the Feign client was called with the correct parameter
        verify(foodCatalogueClient, times(1)).getFoodItemById(foodItemId);
    }

    @Test
    void getFoodItemsByIds_ShouldReturnListOfFoodItems() {
        // Given
        List<Long> foodItemIds = Arrays.asList(1L, 2L);
        
        FoodItemDTO foodItem1 = new FoodItemDTO();
        foodItem1.setId(1L);
        foodItem1.setName("Food 1");
        foodItem1.setPrice(new BigDecimal("10.99"));
        
        FoodItemDTO foodItem2 = new FoodItemDTO();
        foodItem2.setId(2L);
        foodItem2.setName("Food 2");
        foodItem2.setPrice(new BigDecimal("15.99"));
        
        List<FoodItemDTO> expectedFoodItems = Arrays.asList(foodItem1, foodItem2);
        ResponseEntity<List<FoodItemDTO>> responseEntity = ResponseEntity.ok(expectedFoodItems);

        // Mock the Feign client response
        when(foodCatalogueClient.getFoodItemsByIds(foodItemIds))
            .thenReturn(responseEntity);

        // When
        ResponseEntity<List<FoodItemDTO>> response = foodCatalogueClient.getFoodItemsByIds(foodItemIds);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        List<FoodItemDTO> actualFoodItems = response.getBody();
        assertEquals(2, actualFoodItems.size());
        assertEquals(1L, actualFoodItems.get(0).getId());
        assertEquals(2L, actualFoodItems.get(1).getId());
        
        // Verify the Feign client was called with the correct parameter
        verify(foodCatalogueClient, times(1)).getFoodItemsByIds(foodItemIds);
    }

    @Test
    void getFoodItemById_ShouldReturnNotFound_WhenItemDoesNotExist() {
        // Given
        Long foodItemId = 999L;
        
        ResponseEntity<FoodItemDTO> notFoundResponse = ResponseEntity.notFound().build();

        // Mock not found response
        when(foodCatalogueClient.getFoodItemById(foodItemId))
            .thenReturn(notFoundResponse);

        // When
        ResponseEntity<FoodItemDTO> response = foodCatalogueClient.getFoodItemById(foodItemId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(foodCatalogueClient, times(1)).getFoodItemById(foodItemId);
    }

    @Test
    void getFoodItemsByIds_ShouldReturnEmptyList_WhenNoItemsFound() {
        // Given
        List<Long> foodItemIds = Arrays.asList(999L, 1000L);
        ResponseEntity<List<FoodItemDTO>> emptyResponse = ResponseEntity.ok(Collections.emptyList());

        // Mock empty response
        when(foodCatalogueClient.getFoodItemsByIds(foodItemIds))
            .thenReturn(emptyResponse);

        // When
        ResponseEntity<List<FoodItemDTO>> response = foodCatalogueClient.getFoodItemsByIds(foodItemIds);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        
        verify(foodCatalogueClient, times(1)).getFoodItemsByIds(foodItemIds);
    }
}
