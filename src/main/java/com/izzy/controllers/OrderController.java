package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Order;
import com.izzy.payload.request.OrderRequest;
import com.izzy.security.utils.Utils;
import com.izzy.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing order-related operations.
 * Provides endpoints for creating, updating, and retrieving order information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/izzy/orders")
public class OrderController {
    private final OrderService orderService;

    /**
     * Constructor for OrderController.
     *
     * @param orderService the service to manage order operations.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves a list of orders with filtering.
     *
     * @param action     optional filtering parameter
     * @param status     optional filtering parameter
     * @param createdBy  optional filtering parameter
     * @param assignedTo optional filtering parameter
     * @return list of orders.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Order> getOrders(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Long assignedTo) {
        return orderService.getOrders(action, status, createdBy, assignedTo);
    }

    /**
     * Retrieves an order by their ID.
     *
     * @param id the ID of the order to retrieve.
     * @return a ResponseEntity containing the order.
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                return ResponseEntity.ok(order);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Creates a new order.
     *
     * @param orderRequestString the request payload containing order details.
     * @return a ResponseEntity containing a created order details.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Order> createOrder(@RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            Order createdOrder = orderService.createOrder(orderService.getOrderFromOrderRequest(orderRequest, null));
            return ResponseEntity.ok(orderService.getOrderById(createdOrder.getId()));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Updates an existing order.
     *
     * @param id                 the ID of the order to update.
     * @param orderRequestString the request payload containing updated order details.
     * @return ResponseEntity containing an updated order details.
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            Order order = orderService.getOrderFromOrderRequest(orderRequest, id);
            if (id.equals(orderService.updateOrder(order))) {
                return ResponseEntity.ok(order);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Deletes an order by their ID.
     *
     * @param id the ID of the order to delete.
     * @return ResponseEntity containing a success message.
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}