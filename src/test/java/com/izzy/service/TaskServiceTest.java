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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class TaskServiceTest {

    private TaskService taskService;
    private OrderRepository orderRepository;
    private ScooterRepository scooterRepository;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private CustomService customService;
    private NotificationRepository notificationRepository;
    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        scooterRepository = mock(ScooterRepository.class);
        taskRepository = mock(TaskRepository.class);
        userRepository = mock(UserRepository.class);
        customService = mock(CustomService.class);
        notificationRepository = mock(NotificationRepository.class);
        historyService = mock(HistoryService.class);
        taskService = new TaskService(customService, historyService, orderRepository, scooterRepository, taskRepository, notificationRepository);
    }

    @Test
    public void test_getTasksByFiltering_with_valid_filtering_parameters() {

        Long orderId = 1L;
        Long scooterId = 1L;
        String status = "Completed";
        Task task = new Task(orderId, scooterId, 30);
        task.setStatus(status);

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(3L);

        Scooter scooter = new Scooter();
        scooter.setId(scooterId);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(scooterRepository.findById(anyLong())).thenReturn(Optional.of(scooter));
        when(taskRepository.findTasksByFiltering(anyLong(), anyLong(), any(), any())).thenReturn(List.of(task));

        List<?> tasks = taskService.getTasksByFiltering(null, orderId, scooterId, null, status);

        assertNotNull(tasks, "tasks is null");
        assertEquals(1, tasks.size(), "tasks size is incorrect");

        tasks = taskService.getTasksByFiltering(null, orderId, scooterId, "..20", null);

        assertNotNull(tasks, "tasks is null");
        assertEquals(1, tasks.size(), "tasks size is incorrect");
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

        List<Task> tasks = taskService.appendTask(new TaskDTO(task));

        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.get(0).getPriority());
    }

    // Append a task to a non-existent orderId
    @Test
    public void append_task_to_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Task task = new Task(nonExistentOrderId, 1L);
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(taskRepository.findByIdOrderId(anyLong())).thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> taskService.appendTask(new TaskDTO(task)));
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

        taskService.removeTask(new TaskDTO(task));
    }

    @Test
    void remove_task_from_non_existent_order() {
        Long nonExistentOrderId = 999L;
        Long scooterId = 2L;
        Task task = new Task(nonExistentOrderId, scooterId);
        when(customService.checkAllowability(anyLong())).thenReturn(Boolean.TRUE);
        when(taskRepository.findByOrderAndScooterIds(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.removeTask(new TaskDTO(task)));
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