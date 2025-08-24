package com.sattvabite.order.service.impl;

import com.sattvabite.order.dto.OrderDTO;
import com.sattvabite.order.dto.OrderDTOFromFE;
import com.sattvabite.order.entity.Order;
import com.sattvabite.order.entity.OrderItem;
import com.sattvabite.order.exception.ResourceNotFoundException;
import com.sattvabite.order.exception.ServiceException;
import com.sattvabite.order.exception.ValidationException;
import com.sattvabite.order.mapper.OrderMapper;
import com.sattvabite.order.repository.OrderRepository;
import com.sattvabite.order.service.OrderService;
import com.sattvabite.order.service.SequenceGeneratorService;
import com.sattvabite.order.service.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the OrderService interface.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceClient userServiceClient;
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTOFromFE orderRequest) {
        log.info("Creating new order for user: {}", orderRequest.getUserId());
        
        // Validate user exists
        validateUserExists(orderRequest.getUserId());
        
        // Create and save order
        Order order = orderMapper.toEntity(orderRequest);
        order.setOrderId(sequenceGenerator.generateSequence(Order.SEQUENCE_NAME));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // Calculate and set total price
        BigDecimal totalPrice = calculateOrderTotal(order.getOrderItems());
        order.setTotalPrice(totalPrice);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Created order with ID: {}", savedOrder.getId());
        
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(String id) {
        log.debug("Fetching order by ID: {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.debug("Fetching all orders with pagination: {}", pageable);
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        log.debug("Fetching orders for user ID: {}", userId);
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(String id, String status) {
        log.info("Updating status to '{}' for order ID: {}", status, id);
        Order order = getOrderEntityOrThrow(id);
        
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            Order updatedOrder = orderRepository.save(order);
            log.info("Updated status to '{}' for order ID: {}", status, id);
            return orderMapper.toDto(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(String id) {
        log.info("Cancelling order with ID: {}", id);
        Order order = getOrderEntityOrThrow(id);
        
        if (!canCancelOrder(id)) {
            throw new ValidationException("Order cannot be cancelled in its current state");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Cancelled order with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateOrderTotal(String orderId) {
        log.debug("Calculating total for order ID: {}", orderId);
        Order order = getOrderEntityOrThrow(orderId);
        return calculateOrderTotal(order.getOrderItems()).doubleValue();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancelOrder(String orderId) {
        Order order = getOrderEntityOrThrow(orderId);
        return order.getStatus().isCancellable();
    }

    private Order getOrderEntityOrThrow(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    private void validateUserExists(Long userId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (Exception e) {
            log.error("Error validating user with ID: {}", userId, e);
            throw new ServiceException("Error validating user: " + e.getMessage(), e);
        }
    }

    private BigDecimal calculateOrderTotal(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
