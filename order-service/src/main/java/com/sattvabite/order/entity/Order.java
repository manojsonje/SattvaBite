package com.sattvabite.order.entity;

import com.sattvabite.order.dto.FoodItemsDTO;
import com.sattvabite.order.dto.Restaurant;
import com.sattvabite.order.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.Transient;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents an order in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("order")
public class Order {
    public static final String SEQUENCE_NAME = "order_sequence";
    
    @Id
    private String id;
    
    @Positive(message = "Order ID must be a positive number")
    private Long orderId;
    
    // Getters and setters for id and orderId
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    @NotEmpty(message = "At least one food item must be present in the order")
    private List<@Valid FoodItemsDTO> foodItemsList;
    
    @Transient
    private List<OrderItem> orderItems;
    
    @NotNull(message = "Restaurant ID is required")
    @Positive(message = "Restaurant ID must be a positive number")
    private Long restaurantId;
    
    // Getters and setters for orderItems
    public List<OrderItem> getOrderItems() {
        return orderItems != null ? List.copyOf(orderItems) : null;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? List.copyOf(orderItems) : null;
    }
    
    @NotNull(message = "Restaurant information is required")
    private Restaurant restaurant;
    
    // Getter and setter for status
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    // Getter for restaurant
    public Restaurant getRestaurant() {
        return restaurant != null ? new Restaurant(restaurant) : null;
    }
    
    @NotNull(message = "User information is required")
    private UserDTO userDTO;
    
    private OrderStatus status;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Getter and setter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Getter and setter for updatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Version
    private Long version;
    
    @NotNull(message = "Total price is required")
    @PositiveOrZero(message = "Total price must be a positive number or zero")
    private BigDecimal totalPrice;

    /**
     * Enum representing the status of an order.
     */
    public enum OrderStatus {
        CREATED(true),
        PROCESSING(true),
        CONFIRMED(true),
        PREPARING(false),
        READY_FOR_DELIVERY(false),
        OUT_FOR_DELIVERY(false),
        DELIVERED(false),
        CANCELLED(false),
        REFUNDED(false);
        
        private final boolean cancellable;
        
        OrderStatus(boolean cancellable) {
            this.cancellable = cancellable;
        }
        
        public boolean isCancellable() {
            return cancellable;
        }
    }
    
    // Additional getters and setters for better encapsulation
    public List<FoodItemsDTO> getFoodItemsList() {
        return foodItemsList != null ? List.copyOf(foodItemsList) : null;
    }
    
    public void setFoodItemsList(List<FoodItemsDTO> foodItemsList) {
        this.foodItemsList = foodItemsList != null ? List.copyOf(foodItemsList) : null;
    }
    
    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO != null ? new UserDTO(userDTO) : null;
    }

    /**
     * Sets the restaurant for this order.
     *
     * @param restaurant the restaurant to set
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant != null ? new Restaurant(restaurant) : null;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice : BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) &&
               Objects.equals(foodItemsList, order.foodItemsList) &&
               Objects.equals(restaurant, order.restaurant) &&
               Objects.equals(userDTO, order.userDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, foodItemsList, restaurant, userDTO);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", orderId=" + orderId +
                ", foodItemsList=" + foodItemsList +
                ", restaurantId=" + restaurantId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
