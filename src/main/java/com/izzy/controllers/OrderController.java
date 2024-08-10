package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.payload.misk.Task;
import com.izzy.payload.request.OrderRequest;
import com.izzy.payload.response.OrderInfo;
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
    public List<OrderInfo> getOrders(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) Long assignedTo) {
        return orderService.getOrders(action, status, createdBy, assignedTo);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<OrderInfo> getOrderById(@PathVariable Long id) {
        try {
            OrderInfo orderInfo = orderService.getOrderInfoByOrderId(id);
            if (orderInfo != null) {
                return ResponseEntity.ok(orderInfo);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<OrderInfo> createOrder(@RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            OrderInfo orderInfo = orderService.getOrderInfoFromOrderRequest(orderRequest, null);
            OrderInfo createdOrderInfo = orderService.saveOrderInfo(orderInfo);
            return ResponseEntity.ok(createdOrderInfo);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<OrderInfo> updateOrder(@PathVariable Long id, @RequestBody String orderRequestString) {
        try {
            // Validate request body
            OrderRequest orderRequest = (new ObjectMapper()).readValue(orderRequestString, OrderRequest.class);
            // processing
            OrderInfo orderInfo = orderService.getOrderInfoFromOrderRequest(orderRequest, id);
            if (id.equals(orderService.updateOrderInfo(id, orderInfo))) {
                return ResponseEntity.ok(orderService.getOrderInfoByOrderId(id));
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

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Task> getTasksByOrderId(@PathVariable Long id) {
        try {
            return orderService.getOrderInfoByOrderId(id).getTasks();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @DeleteMapping("/{orderId}/tasks/{scooterId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Void> deleteOrderTask(@PathVariable Long orderId, @PathVariable Long scooterId) {
        try {
            orderService.removeTask(orderId, scooterId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}