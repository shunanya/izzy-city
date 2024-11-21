package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.History;
import com.izzy.model.Notification;
import com.izzy.model.Order;
import com.izzy.model.Task;
import com.izzy.repository.NotificationRepository;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.TaskRepository;
import com.izzy.security.custom.service.CustomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final TaskRepository taskRepository;
    private final CustomService customService;
    private final HistoryService historyService;

    public NotificationService(NotificationRepository notificationRepository,
                               OrderRepository orderRepository,
                               TaskRepository taskRepository,
                               CustomService customService,
                               HistoryService historyService) {
        this.notificationRepository = notificationRepository;
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
        this.customService = customService;
        this.historyService = historyService;
    }

    /**
     * Retrieve notifications for the signed-in user (manger or executor).
     *
     * @param taskStatus optional parameter specifying which notifications with the given status should be returned.
     *                   <p>
     *                   Allowed statuses are: {@code completed}, {@code canceled} or {@code active}.
     *                   If this parameter is not defined, the complete existing list of notifications
     *                   for the current user will be returned. ({@code NULL})
     *                   </p>
     * @param userAction optional parameter specifying manager action
     *                   <p>
     *                   Allowed action are {@code approved} and {@code rejected}
     *                   </p>
     * @return List of notifications
     * @throws UnrecognizedPropertyException when specified status or action is not recognized
     */
    public List<Notification> getNotificationsForCurrentUser(@Nullable String userAction, @Nullable String taskStatus) {
        if (taskStatus == null && userAction == null) {
            return notificationRepository.findAllByUserId(customService.currentUserId());
        }
        Integer priority = null;
        if (taskStatus != null && !taskStatus.isBlank()) {
            Task.Status status = Task.Status.getStatusByString(taskStatus);
            if (status == null) {
                throw new UnrecognizedPropertyException("Task status", taskStatus);
            }
            priority = status.getValue();
        }
        if (userAction != null && Notification.Action.UNDEFINED == Notification.Action.getActionByValue(userAction)) {
            throw new UnrecognizedPropertyException("Manager action", userAction);
        }
        return notificationRepository.findNotificationsByFilters(customService.currentUserId(), userAction, priority);
    }

    /**
     * Retrieve notification by id
     *
     * @param id the notification id
     * @return {@link Notification}
     */
    public Notification getNotificationByID(@NonNull Long id) {
        return notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification", "Id", id));
    }

    /**
     * Creates Notification based on {@link Task}
     * <p>
     * Notification will be created only in case when Task is marked (has status) as COMPLETED or CANCELED
     * </p>
     *
     * @param task the task for which the notification should be created.
     * @return saved {@link Notification} instance
     */
    @Transactional
    public Notification createNotification(@NonNull Task task) {
        if (!List.of(Task.Status.CANCELED, Task.Status.COMPLETED).contains(task.getTaskStatus())) {
            throw new NotAcceptableStatusException("Notifications can only be created if the Task status is either 'Completed' or 'Canceled'.");
        }

        Notification notification = notificationRepository.findNotificationByOrderIdAndScooterId(task.getOrderId(), task.getScooterId()).
                orElse(new Notification(customService.currentUserHeadId(), task.getOrderId(), task.getScooterId()));

        if (notification.getId() == null) {
            notification = notificationRepository.save(notification);
            logger.info("Notification created: {}", notification);
            addNotificationHistory(History.Action.CREATE.getValue(), notification.toString());
        }
        return notification;

    }

    /**
     * Update Notification by provided user-manager action
     *
     * @param notificationId the id for notification that has to be updated
     * @param userAction     Available values are one of {@code "rejected"} or {@code "approved"}
     * @return updated {@link Notification}
     */
    @Transactional
    public Notification updateNotification(@NonNull Long notificationId, String userAction) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        if (userAction == null || userAction.isBlank() || Notification.Action.getActionByValue(userAction) == Notification.Action.UNDEFINED) {// userAction is wrong
            throw new NotAcceptableStatusException("Notifications can only be updated if the User action is either 'approved' or 'rejected'.");
        }
        if (!customService.currentUserId().equals(notification.getUserId())) {
            throw new AccessDeniedException("Not allowed to update notifications that do not belong to you.");
        }
        Long orderId = notification.getOrderId();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        Long assignedTo = order.getAssignedTo();
        if (assignedTo == null) {
            throw new BadRequestException("AssignedTo user is not found in the Order");
        }
        notification.setUserAction(userAction); // set user manager action
        notification.setUserId(assignedTo); // reassign notification to executor user
        String managerComment = String.format("Manager %s your decision.", userAction);
        String comment = notification.getTask().getComment();
        if (comment != null) {
            managerComment = String.format("%s%n%s", comment, managerComment);
        }
        notification.getTask().setComment(managerComment);
        return notificationRepository.save(notification);
    }

    /**
     * Handle the situation in case the executor is already familiar with the manager's reaction.
     * <p>This method does the following actions
     * <ul>
     *     <li>remove Task and Notification if manager approved executor action
     *     <li>reassign Task to executor and remove Notification if manager canceled executor action
     * </ul>
     * </p>
     *
     * @param notificationId the id of looked through notification
     */
    @Transactional
    public void actionHandling(@NonNull Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        if (!notification.getUserId().equals(customService.currentUserId())) {
            throw new AccessDeniedException("Not allowed to view notifications that aren't yours.");
        }
        switch (notification.getUserAction().toUpperCase()) {
            case "REJECTED" -> {// reassign task
                notification.getTask().setActive();
                logger.info("Rejected notification: {}", notification);
                addNotificationHistory(History.Action.REJECT.getValue(), notification.toString());
            }
            case "APPROVED" -> {// remove task; corresponding notification will be removed automatically.
                taskRepository.deleteByOrderAndScooterIds(notification.getOrderId(), notification.getScooterId());
                logger.info("Approved notification: {}", notification);
                addNotificationHistory(History.Action.APPROVE.getValue(), notification.toString());
            }
            default -> throw new NotAcceptableStatusException("Unrecognized user action");
        }
        // remove unnecessary notification
//        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteNotificationById(@NonNull Long notificationId) {
        logger.info("Deleting notification: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteNotification(@NonNull Notification notification) {
        logger.info("Deleting notification: {}", notification);
        notificationRepository.delete(notification);
    }

    public void addNotificationHistory(@NonNull String action, @NonNull String msg) {
        historyService.insertHistory(History.Type.NOTIFICATION.getValue(), action, msg);
    }

}
