package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.utils.Utils;
import com.izzy.model.Order;
import com.izzy.model.OrderScooter;
import com.izzy.payload.request.OrderRequest;
import com.izzy.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/izzy/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public List<Order> getOrders(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Long assignedTo) {
        return orderService.getOrders(action, status, createdBy, assignedTo);
    }


    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Order> createOrder(@RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            Order order = orderService.getOrderFromOrderRequest(orderRequest, null);
            Order createdOrder = orderService.saveOrder(order);
//            orderService.updateOrder(createdOrder.getId(), order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            Order order = orderService.getOrderFromOrderRequest(orderRequest, id);
            Order updatedOrder = orderService.updateOrder(id, order);
            if (updatedOrder != null) {
                return ResponseEntity.ok(updatedOrder);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            if (orderService.deleteOrder(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @GetMapping("/{orderId}/scooters")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public List<OrderScooter> getOrderScootersByOrderId(@PathVariable Long orderId) {
        try {
            return orderService.getOrderScootersByOrderId(orderId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PutMapping("/{orderId}/scooters/{scooterId}/priority")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public OrderScooter updatePriority(
            @PathVariable Long orderId,
            @PathVariable Long scooterId,
            @RequestParam Integer priority) {
        try {
            return orderService.updatePriority(orderId, scooterId, priority);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @GetMapping("/{orderId}/scooters/{scooterId}/priority")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public Integer getPriority(
            @PathVariable Long orderId,
            @PathVariable Long scooterId) {
        try {
            return orderService.getPriority(orderId, scooterId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

}