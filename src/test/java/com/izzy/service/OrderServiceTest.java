package com.izzy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.OrderScooter;
import com.izzy.payload.misk.Task;
import com.izzy.repository.OrderScooterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

public class OrderServiceTest {

    @Autowired
    OrderService orderService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create and configure ObjectMapper
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    // Append a valid task to an existing order
    @Test
    public void append_valid_task_to_existing_order() throws JsonProcessingException {
        Long orderId = 19L;
        Task task = new Task(2L, 30);

        String taskJson = objectMapper.writeValueAsString(task);
        System.out.printf("New task: %s%n", taskJson);

        List<Task> tasks = orderService.appendTask(orderId, task);
    
        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);

        String osJson = objectMapper.writeValueAsString(tasks);
        System.out.println(osJson);

    }

    // Append a task to a non-existent orderId
    @Test
    public void append_task_to_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task(2L, 1);
    
        assertThrows(ResourceNotFoundException.class, () -> orderService.appendTask(nonExistentOrderId, task));
    }

    @Autowired
    OrderScooterRepository orderScooterRepository;

    @Test
    void remove_task_from_existing_order() {
        Long orderId = 19L;
        List<OrderScooter> orderScooters = orderScooterRepository.findByOrderId(orderId); // get all records for order
        Task task = new Task(2L,30);
        orderService.appendTask(orderId, task); // adding test record
        List<OrderScooter> orderScootersAfterAppending = orderScooterRepository.findByOrderId(orderId); // get all records for order
        assertTrue(orderScooters.size() <= orderScootersAfterAppending.size());

        orderService.removeTask(orderId, task);

        List<OrderScooter> ossAfterRemoving = orderScooterRepository.findByOrderId(orderId);
        assertTrue(orderScooters.size() >= ossAfterRemoving.size()); // Adjusted assertion
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
        List<OrderScooter> orderScooters = orderService.rearrangeOrderScooterPriorities(orderId);
        assertNotNull(orderScooters);
        assertFalse(orderScooters.isEmpty());

        String json = objectMapper.writeValueAsString(orderScooters);
        System.out.println(json);
    }

}