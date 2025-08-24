package com.sattvabite.order.dto;

import com.sattvabite.order.entity.Order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Data Transfer Object for Order operations.
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Order Data Transfer Object")
public class OrderDTO extends BaseDto {

    @Schema(description = "Unique order identifier", example = "1001")
    @PositiveOrZero(message = "Order ID must be a positive number")
    private Long orderId;
    
    @Schema(description = "List of food items in the order")
    @NotEmpty(message = "At least one food item must be present in the order")
    private List<@Valid FoodItemsDTO> foodItemsList;
    
    @Schema(description = "Restaurant details for the order")
    @NotNull(message = "Restaurant information is required")
    @Valid
    private Restaurant restaurant;
    
    @Schema(description = "User details for the order")
    @NotNull(message = "User information is required")
    @Valid
    private UserDTO userDTO;
    
    @Schema(description = "Current status of the order", example = "CREATED")
    private OrderStatus orderStatus;

    @Schema(description = "Total price of the order", example = "29.99")
    @PositiveOrZero(message = "Total price must be a positive number")
    private Double totalPrice;

    @Override
    public String getId() {
        return id != null ? id : (orderId != null ? orderId.toString() : null);
    }

    @Override
    public void setId(String id) {
        this.id = id;
        if (id != null && this.orderId == null) {
            try {
                this.orderId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                // If id is not a number, just ignore it
            }
        }
    }
    
    // Helper method to set both id and orderId consistently
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
        if (orderId != null) {
            super.setId(orderId.toString());
        } else {
            super.setId(null);
        }
    }
}
