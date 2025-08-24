package com.sattvabite.order.controller;

import com.sattvabite.order.dto.OrderDTO;
import com.sattvabite.order.dto.OrderDTOFromFE;
import com.sattvabite.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Orders.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing food orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Operation(summary = "Create a new order", 
               description = "Creates a new order with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully",
                   content = @Content(schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderDTOFromFE orderRequest) {
        log.info("Received request to create new order for user: {}", orderRequest.getUserId());
        OrderDTO createdOrder = orderService.createOrder(orderRequest);
        log.info("Successfully created order with ID: {}", createdOrder.getId());
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @Operation(summary = "Get order by ID", 
               description = "Retrieves an order by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found",
                   content = @Content(schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable String id) {
        log.debug("Fetching order with ID: {}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Get all orders", 
               description = "Retrieves a paginated list of all orders")
    @ApiResponse(responseCode = "200", 
                description = "Successfully retrieved orders",
                content = @Content(schema = @Schema(implementation = Page.class)))
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching all orders with pagination: {}", pageable);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @Operation(summary = "Get orders by user ID", 
               description = "Retrieves all orders for a specific user")
    @ApiResponse(responseCode = "200", 
                description = "Successfully retrieved user's orders")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        log.debug("Fetching orders for user ID: {}", userId);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @Operation(summary = "Update order status", 
               description = "Updates the status of an existing order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order status updated"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "ID of the order to update", required = true)
            @PathVariable String id,
            @Parameter(description = "New status for the order", required = true)
            @PathVariable String status) {
        log.info("Updating status to '{}' for order ID: {}", status, id);
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancel an order", 
               description = "Cancels an existing order by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid order status for cancellation"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable String id) {
        log.info("Request to cancel order with ID: {}", id);
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
