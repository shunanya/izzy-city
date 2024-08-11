package com.izzy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.*;
import com.izzy.payload.misk.Task;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.UserRepository;
import com.izzy.security.custom.service.CustomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest

public class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private OrderScooterRepository orderScooterRepository;
    private UserRepository userRepository;
    private ScooterRepository scooterRepository;
    private CustomService customService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create and configure ObjectMapper
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        orderRepository = mock(OrderRepository.class);
        orderScooterRepository = mock(OrderScooterRepository.class);
        userRepository = mock(UserRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        customService = mock(CustomService.class);
        orderService = new OrderService(orderRepository, userRepository, scooterRepository, orderScooterRepository, customService);
    }

    // Append a valid task to an existing order
    @Test
    public void append_valid_task_to_existing_order() {
        Long orderId = 19L;
        Task task = new Task(2L, 30);

        Order order = new Order();
        order.setCreatedBy(3L);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderScooterRepository.saveAll(anyIterable())).thenReturn(List.of(new OrderScooter(), new OrderScooter()));
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(customService.checkAllowability(any())).thenReturn(Boolean.TRUE);
        when(scooterRepository.findById(anyLong())).thenReturn(Optional.of(new Scooter()));

        List<Task> tasks = orderService.appendTask(orderId, task);

        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
    }

    // Append a task to a non-existent orderId
    @Test
    public void append_task_to_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task(2L, 1);

        assertThrows(ResourceNotFoundException.class, () -> orderService.appendTask(nonExistentOrderId, task));
    }


    @Test
    void remove_task_from_existing_order() {
        Long orderId = 19L;
        Long scooterId = 2L;
        Task task = new Task(2L, 30);

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(3L);
        Scooter scooter = new Scooter();
        scooter.setId(scooterId);
        OrderScooter orderScooter = new OrderScooter(order, scooter, 30);
        orderScooter.setId(new OrderScooterId(orderId, scooterId));

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(customService.checkAllowability(any())).thenReturn(Boolean.TRUE);
        when(orderScooterRepository.findByOrderAndScooterIds(anyLong(), anyLong())).thenReturn(Optional.of(orderScooter));
        when(orderScooterRepository.findByOrderId(anyLong())).thenReturn(new ArrayList<>(List.of(orderScooter)));
        doNothing().when(orderScooterRepository).deleteByOrderAndScooterIds(anyLong(), anyLong());

        List<Task> tasks = orderService.removeTask(orderId, task);

        assertTrue(tasks != null && !tasks.isEmpty()); // Adjusted assertion
    }

    @Test
    void remove_task_from_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task();
        assertThrows(ResourceNotFoundException.class, () -> orderService.removeTask(nonExistentOrderId, task));
    }

    @Test
    void rearrange_priorities_for_existing_order() throws JsonProcessingException {
        Long orderId = 19L;
        Long scooterId = 2L;
        Order order = new Order();
        order.setId(orderId);
        Scooter scooter = new Scooter();
        scooter.setId(scooterId);
        OrderScooter orderScooter = new OrderScooter(order, scooter, 30);
        orderScooter.setId(new OrderScooterId(orderId, scooterId));

        when(orderScooterRepository.findByOrderId(orderId)).thenReturn(new ArrayList<>(List.of(orderScooter)));
        List<OrderScooter> orderScooters = orderService.rearrangeOrderScooterPriorities(orderId);

        assertNotNull(orderScooters);
        assertFalse(orderScooters.isEmpty());

        String json = objectMapper.writeValueAsString(orderScooters);
        System.out.println(json);
    }

}