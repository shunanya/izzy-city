package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@RestController
@RequestMapping("/izzy/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Order> getOrders(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Long assignedTo) {
        return orderService.getOrders(action, status, createdBy, assignedTo);
    }


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

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Order> createOrder(@RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            Order order = orderService.getOrderFromOrderRequest(orderRequest, null);
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

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
/*

    @GetMapping("/{orderId}/tasks")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Task> getTasksByOrderId(@PathVariable Long orderId) {
        try {
            return orderService.getTasksByOrderId(orderId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PutMapping("/{orderId}/tasks")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Task> appendTask(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            return orderService.appendTask(orderId, task);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @DeleteMapping("/{orderId}/tasks/{scooterId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Void> deleteTaskByOrderScooterIds(@PathVariable Long orderId, @PathVariable Long scooterId) {
        try {
            orderService.removeTask(orderId, scooterId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
*/
}