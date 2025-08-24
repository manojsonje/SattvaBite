package com.sattvabite.order.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * Represents an item within an order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_items")
public class OrderItem {
    
    @Id
    private String id;
    
    @NotBlank(message = "Item name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;
    
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be a positive number or zero")
    private BigDecimal price;
    
    private String specialInstructions;
    
    private boolean isVeg;
    
    @Positive(message = "Restaurant ID must be a positive number")
    private Long restaurantId;
    
    // Getter for price
    public BigDecimal getPrice() {
        return price;
    }
    
    // Getter for quantity
    public Integer getQuantity() {
        return quantity;
    }
    
    // Copy constructor
    public OrderItem(OrderItem other) {
        if (other != null) {
            this.id = other.id;
            this.name = other.name;
            this.description = other.description;
            this.quantity = other.quantity;
            this.price = other.price;
            this.specialInstructions = other.specialInstructions;
            this.isVeg = other.isVeg;
            this.restaurantId = other.restaurantId;
        }
    }
}
