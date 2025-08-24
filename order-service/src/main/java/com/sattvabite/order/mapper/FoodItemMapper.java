package com.sattvabite.order.mapper;

import com.sattvabite.order.dto.FoodItemsDTO;
import com.sattvabite.order.entity.OrderItem;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

/**
 * Mapper for converting between FoodItem DTOs and OrderItem entities.
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = IGNORE)
public interface FoodItemMapper {

    FoodItemMapper INSTANCE = Mappers.getMapper(FoodItemMapper.class);

    /**
     * Converts FoodItemsDTO to OrderItem entity.
     *
     * @param dto the FoodItemsDTO to convert
     * @return the converted OrderItem
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "itemName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "specialInstructions", source = "specialInstructions")
    @Mapping(target = "isVeg", source = "veg")
    @Mapping(target = "restaurantId", source = "restaurantId")
    OrderItem toEntity(FoodItemsDTO dto);

    /**
     * Converts OrderItem entity to FoodItemsDTO.
     *
     * @param entity the OrderItem to convert
     * @return the converted FoodItemsDTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemName", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "specialInstructions", source = "specialInstructions")
    @Mapping(target = "isVeg", source = "veg")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "inStock", constant = "true")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "itemDescription", source = "description")
    FoodItemsDTO toDto(OrderItem entity);

    /**
     * Updates an existing OrderItem with values from FoodItemsDTO.
     *
     * @param dto    the source DTO
     * @param entity the target entity to update
     */
    @Mapping(target = "id", ignore = true) // Prevent ID from being updated
    void updateFromDto(FoodItemsDTO dto, @MappingTarget OrderItem entity);

    /**
     * Converts price from BigDecimal to Long (cents).
     *
     * @param price the price as BigDecimal
     * @return the price in cents as Long
     */
    @Named("toCents")
    default Long toCents(BigDecimal price) {
        return price != null ? price.multiply(BigDecimal.valueOf(100)).longValue() : null;
    }

    /**
     * Converts price from Long (cents) to BigDecimal.
     *
     * @param cents the price in cents
     * @return the price as BigDecimal
     */
    @Named("toBigDecimal")
    default BigDecimal toBigDecimal(Long cents) {
        return cents != null ? BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100)) : null;
    }
}
