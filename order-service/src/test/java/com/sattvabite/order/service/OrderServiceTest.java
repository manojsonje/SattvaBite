package com.sattvabite.order.service;

import com.sattvabite.order.dto.OrderDTO;
import com.sattvabite.order.dto.OrderDTOFromFE;
import com.sattvabite.order.dto.UserDTO;
import com.sattvabite.order.entity.Order;
import com.sattvabite.order.entity.OrderItem;
import com.sattvabite.order.entity.Order.OrderStatus;
import com.sattvabite.order.exception.ResourceNotFoundException;
import com.sattvabite.order.exception.ValidationException;
import com.sattvabite.order.mapper.OrderMapper;
import com.sattvabite.order.repository.OrderRepository;
import com.sattvabite.order.service.client.UserServiceClient;
import com.sattvabite.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private SequenceGeneratorService sequenceGenerator;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderDTO testOrderDTO;
    private OrderDTOFromFE testOrderRequest;
    private final String orderId = "test-order-123";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        // Setup test data
        testOrder = createTestOrder();
        testOrderDTO = createTestOrderDTO();
        testOrderRequest = createTestOrderRequest();
    }

    @Test
    void createOrder_ShouldReturnOrderDTO_WhenRequestIsValid() {
        // Arrange
        when(userServiceClient.getUserById(userId)).thenReturn(new UserDTO());
        when(sequenceGenerator.generateSequence(anyString())).thenReturn(1L);
        when(orderMapper.toEntity(any(OrderDTOFromFE.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.createOrder(testOrderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderDTO.getOrderId(), result.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderDTO.getOrderId(), result.getOrderId());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> orderService.getOrderById("non-existent-id"));
    }

    @Test
    void getAllOrders_ShouldReturnPageOfOrders() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(testOrder), pageable, 1);
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.toDto(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        Page<OrderDTO> result = orderService.getAllOrders(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrderDTO.getOrderId(), result.getContent().get(0).getOrderId());
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus_WhenValidStatus() {
        // Arrange
        String newStatus = "PROCESSING";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        orderService.updateOrderStatus(orderId, newStatus);

        // Assert
        verify(orderRepository, times(1)).save(testOrder);
        assertEquals(OrderStatus.PROCESSING, testOrder.getStatus());
    }

    @Test
    void cancelOrder_ShouldCancelOrder_WhenOrderIsCancellable() {
        // Arrange
        testOrder.setStatus(OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).save(testOrder);
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
    }

    @Test
    void cancelOrder_ShouldThrowException_WhenOrderIsNotCancellable() {
        // Arrange
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> orderService.cancelOrder(orderId));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // Helper methods to create test data
    private Order createTestOrder() {
        Order order = new Order();
        order.setId(orderId);
        order.setOrderId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setTotalPrice(new BigDecimal("29.99"));
        
        OrderItem item = new OrderItem();
        item.setName("Test Item");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("14.99"));
        order.setOrderItems(List.of(item));
        
        return order;
    }

    private OrderDTO createTestOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.setId(orderId);
        dto.setOrderId(1L);
        dto.setOrderStatus(OrderStatus.CREATED);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setTotalPrice(29.99);
        return dto;
    }

    private OrderDTOFromFE createTestOrderRequest() {
        OrderDTOFromFE request = new OrderDTOFromFE();
        request.setUserId(userId);
        // Set other request fields as needed
        return request;
    }
}
