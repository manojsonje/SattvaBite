package com.sattvabite.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data Transfer Object representing a restaurant.
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Restaurant Data Transfer Object")
public class Restaurant extends BaseDto {

    @Override
    public String getId() {
        return id != null ? id : (restaurantId != null ? restaurantId.toString() : null);
    }

    @Override
    public void setId(String id) {
        this.id = id;
        if (id != null && this.restaurantId == null) {
            try {
                this.restaurantId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                // If id is not a number, just ignore it
            }
        }
    }

    @Schema(description = "Unique identifier of the restaurant", example = "1")
    @NotNull(message = "Restaurant ID is required")
    @Positive(message = "Restaurant ID must be a positive number")
    private Long restaurantId;
    
    // Helper method to set both id and restaurantId consistently
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        if (restaurantId != null) {
            super.setId(restaurantId.toString());
        } else {
            super.setId(null);
        }
    }

    @Schema(description = "Name of the restaurant", example = "Pizza Palace", required = true)
    @NotBlank(message = "Restaurant name is required")
    private String name;

    @Schema(description = "Full address of the restaurant", 
            example = "123 Food Street, Cuisine City, FC 12345", 
            required = true)
    @NotBlank(message = "Restaurant address is required")
    private String address;

    @Schema(description = "City where the restaurant is located", 
            example = "Food City")
    private String city;

    @Schema(description = "Contact phone number of the restaurant", 
            example = "+1 (555) 123-4567")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,}$", 
            message = "Please provide a valid phone number")
    private String phoneNumber;

    @Schema(description = "Email address of the restaurant", 
            example = "contact@pizzapalace.com")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", 
            message = "Please provide a valid email address")
    private String email;

    @Schema(description = "Cuisine type of the restaurant", 
            example = "Italian")
    private String cuisineType;

    @Schema(description = "Detailed description of the restaurant")
    private String restaurantDescription;

    @Schema(description = "Whether the restaurant is currently active", 
            example = "true")
    private boolean isActive = true;

    @Schema(description = "Whether the restaurant is currently open for orders", 
            example = "true")
    private boolean isOpen = true;

    @Schema(description = "Average rating of the restaurant (0-5)", 
            example = "4.5")
    @PositiveOrZero(message = "Rating must be a positive number or zero")
    private Double averageRating;

    @Schema(description = "Total number of ratings received", 
            example = "150")
    @PositiveOrZero(message = "Total ratings must be a positive number or zero")
    private Integer totalRatings;

    @Schema(description = "Estimated delivery time in minutes", 
            example = "30")
    @Positive(message = "Delivery time must be a positive number")
    private Integer deliveryTime;

    @Schema(description = "Average delivery time in minutes (legacy field)", 
            example = "30")
    @PositiveOrZero(message = "Delivery time must be a positive number or zero")
    private Integer averageDeliveryTime;

    @Schema(description = "Minimum order amount in currency's smallest unit", 
            example = "1000")
    @PositiveOrZero(message = "Minimum order must be a positive number or zero")
    private Integer minimumOrder;

    @Schema(description = "Minimum order amount for delivery (legacy field)", 
            example = "10.00")
    @PositiveOrZero(message = "Minimum order amount must be a positive number or zero")
    private Double minimumOrderAmount;

    @Schema(description = "Delivery fee in currency's smallest unit", 
            example = "500")
    @PositiveOrZero(message = "Delivery fee must be a positive number or zero")
    private Integer deliveryFee;

    @Schema(description = "List of accepted payment methods")
    private List<String> paymentMethods;

    @Schema(description = "Opening hours of the restaurant by day")
    private Map<String, String> openingHours;

    @Schema(description = "URL of the restaurant's logo", 
            example = "https://example.com/restaurant/logo.jpg")
    private String logoUrl;

    @Schema(description = "List of supported delivery areas (comma-separated)", 
            example = "Downtown, Midtown, Uptown")
    private String deliveryAreas;
    
    /**
     * Copy constructor for Restaurant.
     *
     * @param other the Restaurant to copy from
     */
    public Restaurant(Restaurant other) {
        if (other != null) {
            // Set BaseDto fields
            this.id = other.id;
            this.createdAt = other.createdAt;
            this.updatedAt = other.updatedAt;
            this.version = other.version;
            
            // Set Restaurant fields
            this.restaurantId = other.restaurantId;
            this.name = other.name;
            this.address = other.address;
            this.city = other.city;
            this.phoneNumber = other.phoneNumber;
            this.email = other.email;
            this.cuisineType = other.cuisineType;
            this.restaurantDescription = other.restaurantDescription;
            this.isActive = other.isActive;
            this.isOpen = other.isOpen;
            this.averageRating = other.averageRating;
            this.totalRatings = other.totalRatings;
            this.deliveryTime = other.deliveryTime != null ? other.deliveryTime : other.averageDeliveryTime;
            this.averageDeliveryTime = other.averageDeliveryTime != null ? other.averageDeliveryTime : other.deliveryTime;
            this.minimumOrder = other.minimumOrder != null ? other.minimumOrder : 
                (other.minimumOrderAmount != null ? (int)(other.minimumOrderAmount * 100) : null);
            this.minimumOrderAmount = other.minimumOrderAmount != null ? other.minimumOrderAmount :
                (other.minimumOrder != null ? other.minimumOrder / 100.0 : null);
            this.deliveryFee = other.deliveryFee;
            this.paymentMethods = other.paymentMethods != null ? new ArrayList<>(other.paymentMethods) : null;
            this.openingHours = other.openingHours != null ? new HashMap<>(other.openingHours) : null;
            this.logoUrl = other.logoUrl;
            this.deliveryAreas = other.deliveryAreas;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Restaurant that = (Restaurant) o;
        return isActive == that.isActive && 
               isOpen == that.isOpen &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(name, that.name) &&
               Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), restaurantId, name, address, isActive);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", restaurantDescription='" + restaurantDescription + '\'' +
                '}';
    }
}
