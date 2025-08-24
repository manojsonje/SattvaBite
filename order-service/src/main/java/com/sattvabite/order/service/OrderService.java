package com.sattvabite.order.service;

import com.sattvabite.order.dto.OrderDTO;
import com.sattvabite.order.dto.OrderDTOFromFE;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing orders.
 */
public interface OrderService {

    /**
     * Creates a new order.
     *
     * @param orderRequest the order details
     * @return the created order DTO
     */
    OrderDTO createOrder(OrderDTOFromFE orderRequest);

    /**
     * Retrieves an order by its ID.
     *
     * @param id the order ID
     * @return the order DTO
     * @throws com.sattvabite.order.exception.ResourceNotFoundException if the order is not found
     */
    OrderDTO getOrderById(String id);

    /**
     * Retrieves all orders with pagination.
     *
     * @param pageable the pagination information
     * @return a page of order DTOs
     */
    Page<OrderDTO> getAllOrders(Pageable pageable);

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the user ID
     * @return a list of order DTOs
     */
    List<OrderDTO> getOrdersByUserId(Long userId);

    /**
     * Updates the status of an order.
     *
     * @param id     the order ID
     * @param status the new status
     * @return the updated order DTO
     * @throws com.sattvabite.order.exception.ResourceNotFoundException if the order is not found
     * @throws com.sattvabite.order.exception.ValidationException      if the status transition is invalid
     */
    OrderDTO updateOrderStatus(String id, String status);

    /**
     * Cancels an order.
     *
     * @param id the order ID
     * @throws com.sattvabite.order.exception.ResourceNotFoundException if the order is not found
     * @throws com.sattvabite.order.exception.ValidationException      if the order cannot be cancelled
     */
    void cancelOrder(String id);

    /**
     * Calculates the total price of an order.
     *
     * @param orderId the order ID
     * @return the total price
     * @throws com.sattvabite.order.exception.ResourceNotFoundException if the order is not found
     */
    double calculateOrderTotal(String orderId);

    /**
     * Checks if an order can be cancelled.
     *
     * @param orderId the order ID
     * @return true if the order can be cancelled, false otherwise
     */
    boolean canCancelOrder(String orderId);
}
