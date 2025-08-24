package com.sattvabite.order.controller;

import com.sattvabite.order.dto.OrderDTO;
import com.sattvabite.order.dto.OrderDTOFromFE;
import com.sattvabite.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldReturnCreatedStatus() {
        // Arrange
        OrderDTOFromFE orderDetails = new OrderDTOFromFE();
        OrderDTO savedOrder = new OrderDTO();
        when(orderService.createOrder(orderDetails)).thenReturn(savedOrder);

        // Act
        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDetails);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedOrder, response.getBody());
        verify(orderService, times(1)).createOrder(orderDetails);
    }
}
