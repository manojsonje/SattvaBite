package com.sattvabite.order.repository;

import com.sattvabite.order.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Order entity.
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    /**
     * Find all orders for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of orders for the user
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Find an order by its order ID.
     *
     * @param orderId the order ID
     * @return the order, or null if not found
     */
    Order findByOrderId(Long orderId);
}
