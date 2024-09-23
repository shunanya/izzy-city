package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Notification;
import com.izzy.model.Role;
import com.izzy.model.Task;
import com.izzy.model.User;
import com.izzy.repository.*;
import com.izzy.security.custom.service.CustomService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationServiceTest {

    private final Long orderId = 53L;
    private final Long scooterId1 = 1L;
    private final Long scooterId2 = 2L;
    private final String phoneNumber = "11111111";
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ScooterRepository scooterRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    private User testUser;
    private List<Notification> notifications;
    private List<Task> tasks;


    @BeforeEach
    void setUp() {
        // Create test user
        testUser = userRepository.findByPhoneNumber(phoneNumber).orElse(new User());
        if (testUser.getId() == null) {
            testUser.setPhoneNumber(phoneNumber);
            testUser.setFirstName("Notification Service TestStatus");

            Role adminRole = roleRepository.findByName("Admin").orElseThrow(() ->
                    new ResourceNotFoundException("Role", "name", "Admin"));
            testUser.setRoles(new ArrayList<>() {{
                add(adminRole);
            }});
            testUser = userRepository.save(testUser);
        }

        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).
                orElse(new Task(orderId, scooterId1, Task.Status.CANCELED.getValue()));
        task1.setStatus(Task.Status.CANCELED.toString());
        taskRepository.save(task1);

        Task task2 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId2).
                orElse(new Task(orderId, scooterId2, Task.Status.COMPLETED.getValue()));
        task2.setStatus(Task.Status.COMPLETED.toString());
        taskRepository.save(task2);

        tasks = new ArrayList<>() {{
            add(task1);
            add(task2);
        }};

        notifications = notificationRepository.findNotificationsByFilters(testUser.getId(), null, null);
        assertNotNull(notifications);
        if (notifications.isEmpty()) {
            Notification notification1 = new Notification(null, testUser.getId(), orderId, scooterId1);
            notification1.setUserAction(Notification.Action.APPROVED.getValue());
            notification1.setTask(task1);
            notification1 = notificationRepository.save(notification1);


            Notification notification2 = new Notification(null, testUser.getId(), orderId, scooterId2);
            notification2.setUserAction(Notification.Action.REJECTED.getValue());
            notification2.setTask(task2);
            notification2 = notificationRepository.save(notification2);

            notifications = new ArrayList<>();
            notifications.add(notification1);
            notifications.add(notification2);
        }
    }

    @Test
    @Order(1)
    public void test_retrieve_notifications_for_current_user() {
        // Arrange
        CustomService customService = mock(CustomService.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService/*, scooterRepository*/);

        when(customService.currentUserId()).thenReturn(testUser.getId());
        when(notificationRepository.findNotificationsByFilters(anyLong(), anyString(), anyInt())).thenReturn(notifications);
        when(notificationRepository.findAllByUserId(anyLong())).thenReturn(notifications);

        List<?> actualNotifications = notificationService.getNotificationsForCurrentUser(null, null);

        assertNotNull(actualNotifications);
        assertEquals(2, actualNotifications.size());
    }

    @Test
    @Order(2)
    public void test_retrieve_notifications_for_current_user_and_check_embedded_task() {
        TaskRepository taskRepository = mock(TaskRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService/*, scooterRepository*/);

        when(customService.currentUserId()).thenReturn(testUser.getId());
        when(notificationRepository.findNotificationsByFilters(anyLong(), anyString(), anyInt())).thenReturn(notifications);
        when(notificationRepository.findAllByUserId(anyLong())).thenReturn(notifications);

        List<Notification> actualNotifications = notificationService.getNotificationsForCurrentUser(null, null);

        assertNotNull(actualNotifications);
        assertEquals(2, actualNotifications.size());

        actualNotifications.forEach(n -> {
                assertTrue(notifications.contains(n));
                assertTrue(tasks.contains(n.getTask()));
        });
    }

    // Notification is created when task status is COMPLETED
    @Test
    @Order(3)
    public void test_create_notification_when_task_status_completed() {
        // Arrange
        TaskRepository taskRepository = mock(TaskRepository.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        when(taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1)).thenReturn(Optional.of(tasks.get(0)));

        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).orElse(null);
        assertNotNull(task1);

        task1.setCompleted();

        when(customService.currentUserHeadId()).thenReturn(testUser.getId());
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Notification notification = notificationService.createNotification(task1);

        // Assert
        assertNotNull(notification);
        assertEquals(testUser.getId(), notification.getUserId());
        assertEquals(orderId, notification.getOrderId());
        assertEquals(scooterId1, notification.getScooterId());
    }

    // Task status is neither COMPLETED nor CANCELED
    @Test
    @Order(4)
    public void test_create_notification_when_task_status_not_completed_or_canceled() {
        // Arrange
        TaskRepository taskRepository = mock(TaskRepository.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        when(taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1)).thenReturn(Optional.of(tasks.get(0)));

        Task task1 = taskRepository.findByIdOrderIdAndIdScooterId(orderId, scooterId1).orElse(null);
        assertNotNull(task1);
        task1.setStatus("active");
        taskRepository.save(task1);

        // Act & Assert
        assertThrows(NotAcceptableStatusException.class, () -> notificationService.createNotification(task1));
    }

    @Test
    @Order(5)
    public void test_update_notification_success() {
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        when(customService.currentUserId()).thenReturn(testUser.getId());
        when(notificationRepository.findNotificationsByFilters(anyLong(), anyString(), anyInt())).thenReturn(notifications);
        when(notificationRepository.findAllByUserId(anyLong())).thenReturn(notifications);

        List<Notification> actualNotifications = notificationService.getNotificationsForCurrentUser(null, null);
        assertNotNull(actualNotifications);

        Notification notification = actualNotifications.get(0);
        Long notificationId = notification.getId();
        String userAction = "approved";

        com.izzy.model.Order order = new com.izzy.model.Order();
        order.setAssignedTo(5L);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(orderRepository.findById(notification.getOrderId())).thenReturn(Optional.of(order));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification updatedNotification = notificationService.updateNotification(notificationId, userAction);

        assertNotNull(updatedNotification);
        assertEquals(userAction, updatedNotification.getUserAction());
        assertEquals(order.getAssignedTo(), updatedNotification.getUserId());
    }

    @Test
    @Order(6)
    public void test_update_notification_invalid_notification_id() {
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        Long invalidNotificationId = 999L;
        String userAction = "approved";

        when(notificationRepository.findById(invalidNotificationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.updateNotification(invalidNotificationId, userAction));
    }

    @Test
    @Order(7)
    public void test_actionHandling_With_Valid_Notification_Id(){
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        Notification notification = notifications.get(0);
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(customService.currentUserId()).thenReturn(testUser.getId());

        notificationService.actionHandling(notification.getId());

        assertNotNull(notification.getTask());
        assertEquals(Task.Status.CANCELED, notification.getTask().getTaskStatus());
    }

    @Test
    @Order(8)
    public void test_actionHandling_With_Invalid_Notification_Id(){
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        Long notificationId = 999L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        when(customService.currentUserId()).thenReturn(testUser.getId());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.actionHandling(999L));

         verify(notificationRepository, times(1)). findById(notificationId);
    }

    @Test
    @Order(9)
    public void test_delete_notification_by_valid_id() {
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        CustomService customService = mock(CustomService.class);
        NotificationService notificationService = new NotificationService(notificationRepository, orderRepository, taskRepository, customService);

        Long validNotificationId = 1L;

        doNothing().when(notificationRepository).deleteById(validNotificationId);

        notificationService.deleteNotification(validNotificationId);

        verify(notificationRepository, times(1)).deleteById(validNotificationId);
    }
}
