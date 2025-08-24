package com.sattvabite.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Food Item Data Transfer Object")
public class FoodItemDTO {
    
    @Schema(description = "Unique identifier of the food item", example = "1")
    private Long id;
    
    @Schema(description = "Name of the food item", example = "Margherita Pizza")
    private String name;
    
    @Schema(description = "Description of the food item", example = "Classic pizza with tomato sauce and mozzarella")
    private String description;
    
    @Schema(description = "Price of the food item", example = "12.99")
    private BigDecimal price;
    
    @Schema(description = "ID of the restaurant that serves this food item", example = "101")
    private Long restaurantId;
    
    @Schema(description = "Name of the restaurant that serves this food item", example = "Pizza Palace")
    private String restaurantName;
    
    @Schema(description = "URL of the food item's image", example = "https://example.com/pizza.jpg")
    private String imageUrl;
    
    @Schema(description = "Category of the food item", example = "PIZZA")
    private String category;
    
    @Schema(description = "Whether the food item is currently available", example = "true")
    private Boolean isAvailable;
    
    @Schema(description = "Average rating of the food item (1-5)", example = "4.5")
    private Double averageRating;
    
    @Schema(description = "Number of ratings received for this food item", example = "42")
    private Integer ratingCount;
    
    @Schema(description = "Preparation time in minutes", example = "20")
    private Integer preparationTime;
    
    @Schema(description = "List of ingredients (comma-separated)", example = "Tomato sauce, Mozzarella, Basil")
    private String ingredients;
    
    @Schema(description = "Whether the food item is vegetarian", example = "true")
    private Boolean isVegetarian;
    
    @Schema(description = "Whether the food item is vegan", example = "false")
    private Boolean isVegan;
    
    @Schema(description = "Whether the food item is gluten-free", example = "false")
    private Boolean isGlutenFree;
    
    @Schema(description = "Whether the food item is spicy", example = "false")
    private Boolean isSpicy;
}
