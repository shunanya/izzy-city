package com.izzy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.*;
import com.izzy.model.misk.Task;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.OrderScooterRepository;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.UserRepository;
import com.izzy.security.custom.service.CustomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    private TaskService taskService;
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
        taskService = new TaskService(customService, /*orderRepository,*/ scooterRepository, orderScooterRepository);
    }

    // Append a valid task to an existing order
    @Test
    public void append_valid_task_to_existing_order() {
        Long orderId = 19L;
        Long scooterId = 2L;
        Task task = new Task(scooterId, 30);

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(3L);
        Scooter scooter = new Scooter();
        scooter.setId(scooterId);
        OrderScooter orderScooter = new OrderScooter(order, scooter, 300);
        orderScooter.setId(new OrderScooterId(orderId, scooterId));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderScooterRepository.findByOrderId(orderId)).thenReturn(new ArrayList<>(Arrays.asList(orderScooter)));
        when(orderScooterRepository.saveAll(anyIterable())).thenReturn(List.of(new OrderScooter(), new OrderScooter()));
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(scooterRepository.findById(anyLong())).thenReturn(Optional.of(new Scooter()));

        List<Task> tasks = taskService.appendTask(orderId, task);

        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
        assertEquals(1, tasks.get(0).getPriority());
    }

    // Append a task to a non-existent orderId
    @Test
    public void append_task_to_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task(2L, 1);
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(orderScooterRepository.findByOrderId(anyLong())).thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> taskService.appendTask(nonExistentOrderId, task));
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
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(orderScooterRepository.findByOrderAndScooterIds(anyLong(), anyLong())).thenReturn(Optional.of(orderScooter));
        when(orderScooterRepository.findByOrderId(anyLong())).thenReturn(new ArrayList<>(List.of(orderScooter)));
        doNothing().when(orderScooterRepository).deleteByOrderAndScooterIds(anyLong(), anyLong());

        List<Task> tasks = taskService.removeTask(orderId, task);

        assertTrue(tasks != null && !tasks.isEmpty()); // Adjusted assertion
    }

    @Test
    void remove_task_from_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task();
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(orderScooterRepository.findByOrderAndScooterIds(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.removeTask(nonExistentOrderId, task));
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

        List<OrderScooter> oss = new ArrayList<>(){{
            add(new OrderScooter(order, scooter, 0));
            add(new OrderScooter(order, scooter, 10));
            add(new OrderScooter(order, scooter, 20));
            add(new OrderScooter(order, scooter, -1));
        }};
        when(orderScooterRepository.findByOrderId(orderId)).thenReturn(oss);
        List<OrderScooter> orderScooters = taskService.rearrangeOrderScooterPriorities(orderId);

        assertNotNull(orderScooters);
        assertFalse(orderScooters.isEmpty());

        orderScooters.sort(Comparator.comparingInt(OrderScooter::getPriority));
        for (int i = 0; i < orderScooters.size(); i++) {
            assertTrue(orderScooters.get(i).getPriority() == i - 1);
        }
        orderScooters.forEach(os->{
            System.out.printf("%s ", os.getPriority());
        });
    }

}