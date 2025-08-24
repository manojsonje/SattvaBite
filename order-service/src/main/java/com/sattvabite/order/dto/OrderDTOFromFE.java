package com.sattvabite.order.dto;

import com.sattvabite.order.entity.Order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for creating new orders from the frontend.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Order Data Transfer Object for frontend requests")
public class OrderDTOFromFE extends BaseDto {

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Schema(description = "List of food items in the order", required = true)
    @NotEmpty(message = "At least one food item must be present in the order")
    private List<@Valid FoodItemsDTO> foodItemsList;

    @Schema(description = "ID of the user placing the order", example = "123", required = true)
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @Schema(description = "Restaurant details for the order", required = true)
    @NotNull(message = "Restaurant information is required")
    private Restaurant restaurant;

    @Schema(description = "Special instructions for the order", example = "No onions, please")
    private String specialInstructions;

    @Schema(description = "Delivery address for the order", example = "123 Main St, City, Country")
    private String deliveryAddress;

    @Schema(description = "Contact phone number", example = "+1234567890")
    private String contactPhone;

    @Schema(description = "Initial status of the order", example = "CREATED")
    private OrderStatus status = OrderStatus.CREATED;

    /**
     * Get a copy of the food items list to ensure immutability.
     * @return A copy of the food items list or null if the list is null
     */
    public List<FoodItemsDTO> getFoodItemsList() {
        return foodItemsList != null ? List.copyOf(foodItemsList) : null;
    }

    /**
     * Get the restaurant associated with this order.
     * @return The restaurant or null if not set
     */
    public Restaurant getRestaurant() {
        return restaurant != null ? new Restaurant(restaurant) : null;
    }

    /**
     * Get the user ID associated with this order.
     * @return The user ID or null if not set
     */
    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDTOFromFE that = (OrderDTOFromFE) o;
        return Objects.equals(foodItemsList, that.foodItemsList) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(restaurant, that.restaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodItemsList, userId, restaurant);
    }

    @Override
    public String toString() {
        return "OrderDTOFromFE{" +
                "foodItemsList=" + foodItemsList +
                ", userId=" + userId +
                ", restaurant=" + restaurant +
                '}';
    }
}
