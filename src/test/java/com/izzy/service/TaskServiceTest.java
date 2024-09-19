package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Order;
import com.izzy.model.Scooter;
import com.izzy.model.Task;
import com.izzy.model.User;
import com.izzy.model.TaskDTO;
import com.izzy.repository.*;
import com.izzy.security.custom.service.CustomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskServiceTest {

    private TaskService taskService;
    private OrderRepository orderRepository;
    private ScooterRepository scooterRepository;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private CustomService customService;
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        customService = mock(CustomService.class);
        notificationRepository = mock(NotificationRepository.class);
        taskService = new TaskService(customService, orderRepository, scooterRepository, taskRepository, notificationRepository);
    }

    // Append a valid task to an existing order
    @Test
    public void append_valid_task_to_existing_order() {
        Long orderId = 19L;
        Long scooterId = 2L;
        Task task = new Task(orderId, scooterId, 30);

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(3L);

        Scooter scooter = new Scooter();
        scooter.setId(scooterId);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(scooterRepository.findById(anyLong())).thenReturn(Optional.of(scooter));
        when(taskRepository.deleteAllByIdOrderId(anyLong())).thenReturn(1);
        doNothing().when(taskRepository).flush();
        when(taskRepository.saveAll(anyIterable())).thenReturn(List.of(new Task(), new Task()));
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);

        List<Task> tasks = taskService.appendTask(orderId, new TaskDTO(task));

        assertNotNull(tasks);
        assertTrue(tasks.size() > 0);
        assertEquals(1, tasks.get(0).getPriority());
    }

    // Append a task to a non-existent orderId
    @Test
    public void append_task_to_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task(nonExistentOrderId, 1L);
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(taskRepository.findByIdOrderId(anyLong())).thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> taskService.appendTask(nonExistentOrderId, new TaskDTO(task)));
    }


    @Test
    void remove_task_from_existing_order() {
        Long orderId = 19L;
        Long scooterId = 2L;
        Task task = new Task(orderId, scooterId, 30);

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(3L);
        Scooter scooter = new Scooter();
        scooter.setId(scooterId);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(taskRepository.findByIdOrderId(anyLong())).thenReturn(new ArrayList<>(List.of(task)));
        when(taskRepository.findByOrderAndScooterIds(orderId, scooterId)).thenReturn(Optional.of(task));
        when(taskRepository.deleteByOrderAndScooterIds(anyLong(), anyLong())).thenReturn(1);

        taskService.removeTask(orderId, new TaskDTO(task));
    }

    @Test
    void remove_task_from_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Long scooterId = 2L;
        Task task = new Task(nonExistentOrderId, scooterId);
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(taskRepository.findByOrderAndScooterIds(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.removeTask(nonExistentOrderId, new TaskDTO(task)));
    }

    @Test
    void rearrange_priorities_for_existing_order() {
        Long orderId = 19L;
        Long scooterId = 2L;

        List<Task> tasks = new ArrayList<>(){{
            add(new Task(orderId, scooterId, 0));
            add(new Task(orderId, scooterId, 10));
            add(new Task(orderId, scooterId, 20));
            add(new Task(orderId, scooterId, -1));
        }};
        Order order = new Order();
        order.setTasks(tasks);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(taskRepository.findByIdOrderId(orderId)).thenReturn(tasks);
        when(customService.checkAllowability(order)).thenReturn(Boolean.TRUE);
        List<Task> rearrangedTasks = taskService.rearrangeTaskPriorities(orderId);

        assertNotNull(rearrangedTasks);
        assertFalse(rearrangedTasks.isEmpty());

        tasks.sort(Comparator.comparingInt(Task::getPriority));
        for (int i = 0; i < rearrangedTasks.size(); i++) {
            assertEquals((int) rearrangedTasks.get(i).getPriority(), i - 1);
        }
        rearrangedTasks.forEach(t-> System.out.printf("%s ", t.getPriority()));
    }

}