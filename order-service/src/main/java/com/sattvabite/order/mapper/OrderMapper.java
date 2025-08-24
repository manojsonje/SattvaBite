package com.sattvabite.order.mapper;

import com.sattvabite.order.dto.*;
import com.sattvabite.order.entity.Order;
import com.sattvabite.order.entity.OrderItem;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Mapper for converting between Order entities and DTOs.
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrderMapper {

    @Autowired
    protected FoodItemMapper foodItemMapper;

    @Mapping(target = "id", source = "orderId")
    @Mapping(target = "foodItemsList", source = "orderItems")
    @Mapping(target = "restaurant", source = "restaurant")
    @Mapping(target = "userDTO", source = "userDTO")
    public abstract OrderDTO toDto(Order order);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "orderItems", source = "foodItemsList")
    @Mapping(target = "restaurantId", source = "restaurant.restaurantId")
    @Mapping(target = "userDTO", source = "userDTO")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    public abstract Order toEntity(OrderDTO dto);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "orderItems", source = "foodItemsList")
    @Mapping(target = "restaurantId", source = "restaurant.restaurantId")
    @Mapping(target = "userDTO.userId", source = "userId")
    @Mapping(target = "status", expression = "java(com.sattvabite.order.entity.Order.OrderStatus.CREATED)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "version", constant = "0L")
    public abstract Order toEntity(OrderDTOFromFE dto);

    /**
     * Maps a restaurant ID to a Restaurant object.
     * This implementation creates a minimal Restaurant DTO with just the ID and name.
     */
    protected Restaurant mapRestaurantIdToRestaurant(Long restaurantId, String name) {
        if (restaurantId == null) {
            return null;
        }
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .name(name != null ? name : "")
                .build();
    }
    
    /**
     * Gets the restaurant ID from a Restaurant object.
     */
    protected Long getRestaurantId(Restaurant restaurant) {
        return restaurant != null ? restaurant.getRestaurantId() : null;
    }

    /**
     * Maps a restaurant ID and name to a Restaurant object.
     * This is a convenience method for mapping operations.
     */
    protected Restaurant mapToRestaurant(Long restaurantId, String name) {
        return mapRestaurantIdToRestaurant(restaurantId, name);
    }

    protected List<OrderItem> mapFoodItems(List<FoodItemsDTO> foodItems) {
        if (foodItems == null) {
            return null;
        }
        return foodItems.stream()
                .map(foodItemMapper::toEntity)
                .collect(Collectors.toList());
    }

    protected List<FoodItemsDTO> mapOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        return orderItems.stream()
                .map(foodItemMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * After mapping callback to set additional properties on the target DTO.
     */
    @AfterMapping
    protected void afterMapping(Order source, @MappingTarget OrderDTO target) {
        // Ensure the restaurant is set if not already set from the restaurant object
        if (source.getRestaurant() != null && target.getRestaurant() == null) {
            Restaurant restaurant = Restaurant.builder()
                .restaurantId(source.getRestaurant().getRestaurantId())
                .name(source.getRestaurant().getName())
                .build();
            target.setRestaurant(restaurant);
        }
    }
    
    @AfterMapping
    protected void afterMapping(OrderDTO source, @MappingTarget Order target) {
        // Set the restaurant from restaurant object if not already set
        if (source.getRestaurant() != null) {
            target.setRestaurantId(source.getRestaurant().getRestaurantId());
        }
    }
}
