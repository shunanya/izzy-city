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
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
//@DataJpaTest
@Transactional
//@Transactional(isolation = Isolation.READ_COMMITTED)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NotificationRepositoryTest {

    private final String phoneNumber = "11111111";
    private final Long scooterId = 1L;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrderRepository orderRepository;
    private Notification testNotification;
    private User testUser;
    private Long orderId = 1L;
    private String userAction = Notification.Action.REJECTED.getValue();

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = userRepository.findByPhoneNumber(phoneNumber).orElse(new User());
        if (testUser.getId() == null) {
            testUser.setPhoneNumber(phoneNumber);
            testUser.setFirstName("Notification Repository TestStatus");

            Role adminRole = roleRepository.findByName("Admin").orElseThrow(() ->
                    new ResourceNotFoundException("Role", "name", "Admin"));
            testUser.setRoles(new ArrayList<>(List.of(adminRole)));

            testUser = userRepository.save(testUser);
        }

        // Create order
        com.izzy.model.Order order = orderRepository.findById(orderId)
                .orElse(new com.izzy.model.Order());
        order.setAssignedTo(6L);
        order.setName("test_order");
        order.setAction("Move");
        order.setStatus("Created");
        order = orderRepository.save(order);

        assertNotNull(order);
        orderId = order.getId();

        // Create test task
        Task task = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId).
                orElse(new Task(orderId, scooterId, Task.Status.CANCELED.getValue()));
        task.setStatus(Task.Status.CANCELED.toString());
        task = taskRepository.save(task);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        order.setTasks(tasks);
        orderRepository.save(order);

        // Create test notification
        List<Notification> notifications = notificationRepository.findAllByUserId(testUser.getId());
        assertNotNull(notifications);
        Notification testNotification;
        if (notifications.isEmpty()) {
            testNotification = new Notification(null, testUser.getId(), task.getOrderId(), task.getScooterId());
        } else {
            testNotification = notifications.get(0);
        }
        testNotification.setUserAction(Notification.Action.REJECTED.getValue());
        testNotification.setTask(task);
        testNotification = notificationRepository.save(testNotification);

        this.testNotification = testNotification;
        assertNotNull(testNotification);
    }

    @Test
    @Order(3)
    void findAllByUserId_ShouldReturnAllNotificationsAssociatedWithUser() {
        List<Notification> notifications = notificationRepository.findAllByUserId(testUser.getId());

        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size()); // Assuming only one notification per user
        assertEquals(testNotification, notifications.get(0));
    }

    @Test
    @Order(4)
//    @Transactional(isolation = Isolation.READ_COMMITTED)
    void findNotificationsByUserIdAndPriorities() {
        List<Integer> priorities = new ArrayList<>() {{
            add(Task.Status.COMPLETED.getValue());
            add(Task.Status.CANCELED.getValue());
        }};
        assertFalse(priorities.isEmpty());
        List<Notification> notifications = notificationRepository.findNotificationsByFilters(testUser.getId(), Notification.Action.REJECTED.getValue(), Task.Status.CANCELED.getValue());

        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());

        Optional<Notification> optionalNotification = notificationRepository.findById(notifications.get(0).getId());
        assertTrue(optionalNotification.isPresent());
    }

    @Test
    @Order(6)
    void findByUserAction_ShouldReturnNotificationsWithSpecificUserAction() {
        List<Notification> notifications = notificationRepository.findAllByUserAction(userAction);

        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size()); // Assuming only one notification with this user action
        assertEquals(testNotification, notifications.get(0));
    }

    @Test
    @Order(7)
    void updateUserAction_ShouldReturnNotificationsWithSpecificUserAction() {
        List<Notification> notifications = notificationRepository.findNotificationsByFilters(null, userAction, null);
        assertNotNull(notifications);
        assert (notifications.size() > 0);

        userAction = Notification.Action.APPROVED.getValue();
        Notification notification = notifications.get(0);
        notification.setUserAction(userAction);
        notification = notificationRepository.save(notification);
        assertNotNull(notification);

        notification = notificationRepository.findById(notification.getId()).orElse(null);
        assertNotNull(notification);
        assertEquals(userAction, notification.getUserAction());
    }

    @Test
    @Order(8)
    void findNotificationsByFilters() {
        List<Notification> notifications = notificationRepository.findNotificationsByFilters(testUser.getId(), "canceled", null);

        assertNotNull(notifications);
        assertEquals(0, notifications.size());
    }

    @Test
    @Order(9)
//    @Commit
    void deleteNotification_ShouldReturnEmptyList() {
        List<Notification> notifications = notificationRepository.findNotificationsByFilters(testUser.getId(), null, null);
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());

        notificationRepository.delete(notifications.get(0));

        Optional<Notification> optionalNotification = notificationRepository.findById(notifications.get(0).getId());
        assertFalse(optionalNotification.isPresent());
    }
}
