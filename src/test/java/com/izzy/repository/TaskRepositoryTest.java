package com.izzy.repository;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Notification;
import com.izzy.model.Role;
import com.izzy.model.Task;
import com.izzy.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    private Long orderId = 1L;
    private final Long scooterId1 = 1L;
    private final Long scooterId2 = 2L;
    private final String phoneNumber = "11111111";
    private User testUser;
    private com.izzy.model.Order order;
    private List<Notification> notifications;
    private List<Task> tasks;

    private void createUser(){
        // Create test user
        testUser = new User(null, "test-user", phoneNumber);
        Role adminRole = roleRepository.findByName("Admin").orElseThrow(() ->
                new ResourceNotFoundException("Role", "name", "Admin"));
        testUser.setRoles(new ArrayList<>() {{
            add(adminRole);
        }});
        testUser = userRepository.save(testUser);
        testUser.setUserManager(testUser.getId());
        userRepository.save(testUser);
    }

    private void createOrder(){
        order = new com.izzy.model.Order(null, "test-order", "Move", "Created");
        order.setAssignedTo(testUser.getId());
        order = orderRepository.save(order);
        orderId = order.getId();
    }

    private void createTasks(){
        tasks = new ArrayList<>();
        Task task1 = new Task(orderId, scooterId1, Task.Status.CANCELED.getValue());
        task1.setStatus(Task.Status.CANCELED.toString());
        tasks.add(taskRepository.save(task1));
        assertEquals(orderId, tasks.get(0).getOrderId());

        Task task2 = new Task(orderId, scooterId2, Task.Status.COMPLETED.getValue());
        task2.setStatus(Task.Status.COMPLETED.toString());
        tasks.add(taskRepository.save(task2));
        assertEquals(orderId, tasks.get(1).getOrderId());
    }

    @BeforeEach
    void setUp() {
        createUser();
        createOrder();
        createTasks();
        order.setTasks(tasks);
        order = orderRepository.save(order);
        orderId = order.getId();
    }

    @Test
    @Order(1)
    void findTasksByUserId() {
        List<Task> taskList = taskRepository.findTasksByUserManagerId(testUser.getId());

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
    }

    @Test
    @Order(2)
    void findTasksByAssignedUserId() {
        List<Task> taskList = taskRepository.findByAssignedUserId(testUser.getId());

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
        assertTrue(taskList.size() > 0);
    }

    @Test
    @Order(3)
    void findFilteredTasksByAssignedUserId() {
//        List<Task> taskList = taskRepository.findFilteredTasksByAssignedUserId(6L, new ArrayList<>(){{add(Task.Status.CANCELED.getValue());}});
        List<Task> taskList = taskRepository.findAllNonActiveTasksByAssignedUserId(testUser.getId());

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
    }

    @Test
    @Order(4)
    void findFilteredTasksByAssignedUserId_Without_Status_Defined() {
        List<Task> taskList = taskRepository.findFilteredTasksByAssignedUserId(testUser.getId(), null);

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
    }

    @Test
    @Order(5)
    void statusTaskCheck() {
        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).
                orElse(new Task(orderId, scooterId1, Task.Status.CANCELED.getValue()));
        task1.setStatus(Task.Status.CANCELED.toString());
        taskRepository.save(task1);

        task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).orElse(null);
        assertNotNull(task1);
        assertNotNull(task1.getStatus());
        assert ("canceled".equalsIgnoreCase(task1.getTaskStatusString()));

        task1.setStatus(Task.Status.ACTIVE.toString());
        taskRepository.save(task1);

        task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).orElse(null);
        assertNotNull(task1);
        assertNotNull(task1.getStatus());
        assertTrue ("Active".equalsIgnoreCase(task1.getTaskStatusString()));
    }

    @Test
    @Order(6)
//    @Commit        // or @Rollback(false)
    void removeTaskAndCheck() {
        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1)
                .orElse(new Task(orderId, scooterId1, Task.Status.CANCELED.getValue()));
        assertNotNull(task1);
        taskRepository.save(task1);

        // Perform the delete operation
        int deletedRecords = taskRepository.deleteByOrderAndScooterIds(task1.getOrderId(), task1.getScooterId());

        // Verify that the task is deleted
        assertEquals(1, deletedRecords);
        task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).orElse(null);
        assertNull(task1);
    }

    @Test
    public void testTaskByFiltering() {
        List<Task> tasks = taskRepository.findTasksByFiltering(orderId, scooterId1, -1, null);
        assertNotNull(tasks, "Cannot be null");
        assertFalse(tasks.isEmpty(), "Cannot be empty");
    }
}