package com.izzy.controller;

import com.izzy.controllers.OrderController;
import com.izzy.payload.response.OrderInfo;
import com.izzy.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    void setUp(){
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
    }

    // Retrieve all orders successfully
    @Test
    public void test_retrieve_all_orders_successfully() {
        List<OrderInfo> mockOrders = List.of(new OrderInfo(), new OrderInfo());
        when(orderService.getOrders(null, null, null, null)).thenReturn(mockOrders);
    
        List<OrderInfo> orders = orderController.getOrders(null, null, null, null);
    
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    // Handle null or malformed order data
    @Test
    public void test_handle_null_or_malformed_order_data() {
        when(orderService.getOrders(null, null, null, null)).thenReturn(null);
    
        List<OrderInfo> orders = orderController.getOrders(null, null, null, null);
    
        assertNull(orders);
    }

}