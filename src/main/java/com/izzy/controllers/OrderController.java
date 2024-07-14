package com.izzy.controllers;

import com.izzy.model.OrderEntity;
import com.izzy.model.OrderScooterEntity;
import com.izzy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderEntity> getOrders(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Long assignedTo) {
        return orderService.getOrders(action, status, createdBy, assignedTo);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable Long id) {
        OrderEntity order = orderService.getOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity order) {
        OrderEntity createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderEntity> updateOrder(@PathVariable Long id, @RequestBody OrderEntity order) {
        OrderEntity updatedOrder = orderService.updateOrder(id, order);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orderService.deleteOrder(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{orderId}/scooters")
    public List<OrderScooterEntity> getOrderScootersByOrderId(@PathVariable Long orderId) {
        return orderService.getOrderScootersByOrderId(orderId);
    }

    @PutMapping("/{orderId}/scooters/{scooterId}/priority")
    public OrderScooterEntity updatePriority(
            @PathVariable Long orderId,
            @PathVariable Long scooterId,
            @RequestParam Integer priority) {
        return orderService.updatePriority(orderId, scooterId, priority);
    }

    @GetMapping("/{orderId}/scooters/{scooterId}/priority")
    public Integer getPriority(
            @PathVariable Long orderId,
            @PathVariable Long scooterId) {
        return orderService.getPriority(orderId, scooterId);
    }

}