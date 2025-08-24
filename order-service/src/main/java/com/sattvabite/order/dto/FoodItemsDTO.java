package com.sattvabite.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a food item in an order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Food Item Data Transfer Object")
public class FoodItemsDTO {

    @Schema(description = "Unique identifier of the food item", example = "101")
    @NotNull(message = "Food item ID is required")
    @Positive(message = "Food item ID must be a positive number")
    private Long id;

    @Schema(description = "Name of the food item", example = "Margherita Pizza", required = true)
    @NotBlank(message = "Food item name is required")
    private String itemName;

    @Schema(description = "Description of the food item", example = "Classic pizza with tomato sauce and mozzarella")
    private String description;

    @Schema(description = "Quantity of this item in the order", example = "2", required = true)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @Schema(description = "Price per unit of the food item", example = "12.99", required = true)
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be a positive number or zero")
    private BigDecimal price;

    @Schema(description = "Special instructions for this food item", example = "No cheese, extra sauce")
    private String specialInstructions;

    @Schema(description = "Whether the item is in stock", example = "true")
    private boolean inStock = true;

    @Schema(description = "Category of the food item", example = "PIZZA")
    private String category;
    
    @Schema(description = "Description of the food item")
    private String itemDescription;
    
    @Schema(description = "Whether the item is vegetarian", example = "true")
    private boolean isVeg;
    
    @Schema(description = "ID of the restaurant this item belongs to")
    private Long restaurantId;

}
