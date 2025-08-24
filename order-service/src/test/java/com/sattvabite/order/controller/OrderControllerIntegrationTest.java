package com.sattvabite.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sattvabite.order.config.OrderControllerTestConfig;
import com.sattvabite.order.dto.OrderDTO;
import com.sattvabite.order.dto.OrderDTOFromFE;
import com.sattvabite.order.entity.Order.OrderStatus;
import com.sattvabite.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(OrderControllerTestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.discovery.enabled=false",
    "eureka.client.enabled=false",
    "spring.data.mongodb.auto-index-creation=false",
    "spring.main.allow-bean-definition-overriding=true",
    "management.endpoints.web.exposure.include=*"
})
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderDTO orderDTO;
    private OrderDTOFromFE orderRequest;
    private static final String ORDER_ID = "test-order-123";
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup test data
        orderDTO = new OrderDTO();
        orderDTO.setId(ORDER_ID);
        orderDTO.setOrderId(1L);
        orderDTO.setTotalPrice(29.99); 
        orderDTO.setCreatedAt(LocalDateTime.now());
        orderDTO.setUpdatedAt(LocalDateTime.now());

        orderRequest = new OrderDTOFromFE();
        orderRequest.setUserId(USER_ID);
    }

    @Test
    void createOrder_ShouldReturnCreated() throws Exception {
        when(orderService.createOrder(any(OrderDTOFromFE.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ORDER_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(orderService, times(1)).createOrder(any(OrderDTOFromFE.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(ORDER_ID)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/v1/orders/{id}", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORDER_ID))
                .andExpect(jsonPath("$.userId").value(USER_ID));

        verify(orderService, times(1)).getOrderById(ORDER_ID);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() throws Exception {
        String newStatus = "PROCESSING";
        OrderDTO updatedOrder = new OrderDTO();
        updatedOrder.setId(ORDER_ID);
        updatedOrder.setOrderId(1L);
        updatedOrder.setTotalPrice(29.99); 
        
        when(orderService.updateOrderStatus(ORDER_ID, newStatus)).thenReturn(updatedOrder);

        mockMvc.perform(patch("/api/v1/orders/{id}/status/{status}", ORDER_ID, newStatus)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus));

        verify(orderService, times(1)).updateOrderStatus(ORDER_ID, newStatus);
    }

    @Test
    void cancelOrder_ShouldReturnOk() throws Exception {
        doNothing().when(orderService).cancelOrder(ORDER_ID);

        mockMvc.perform(post("/api/v1/orders/{id}/cancel", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderService, times(1)).cancelOrder(ORDER_ID);
    }

    @Test
    void getOrdersByUserId_ShouldReturnOrders() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDTO> page = new PageImpl<>(List.of(orderDTO), pageable, 1);
        
when(orderService.getOrdersByUserId(USER_ID)).thenReturn(List.of(orderDTO));

mockMvc.perform(get("/api/v1/orders/user/{userId}", USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ORDER_ID));

        verify(orderService, times(1)).getOrdersByUserId(USER_ID);
    }

    @Test
    void getAllOrders_ShouldReturnOrderPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderDTO> page = new PageImpl<>(List.of(orderDTO), pageable, 1);
        
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/orders")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ORDER_ID))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(orderService, times(1)).getAllOrders(any(Pageable.class));
    }
}
