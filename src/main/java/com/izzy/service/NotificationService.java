package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Notification;
import com.izzy.model.Order;
import com.izzy.model.Task;
import com.izzy.repository.NotificationRepository;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.TaskRepository;
import com.izzy.security.custom.service.CustomService;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final TaskRepository taskRepository;
    private final CustomService customService;

    public NotificationService(NotificationRepository notificationRepository,
                               OrderRepository orderRepository,
                               TaskRepository taskRepository,
                               CustomService customService) {
        this.notificationRepository = notificationRepository;
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
        this.customService = customService;
    }

    /**
     * Retrieve notifications for the signed-in user (manger or executor).
     *
     * @param taskStatus optional parameter specifying which notifications with the given status should be returned.
     *                   Allowed statuses are: {@code completed}, {@code canceled} or {@code active}.
     *                   If this parameter is not defined, the complete existing list of notifications
     *                   for the current user will be returned. ({@code NULL})
     * @return List of notifications
     * @throws BadRequestException when specified status is not recognized
     */
    public List<Notification> getNotificationsForCurrentUser(@Nullable String userAction, @Nullable String taskStatus) {
        if (taskStatus != null) {
            if (Task.Status.getStatusByString(taskStatus) == null) {
                throw new BadRequestException(String.format("Unknown specified status: %s", taskStatus));
            }

            Integer priority = Task.Status.getStatusByString(taskStatus).getValue();
            return notificationRepository.findNotificationsByFilters(customService.currentUserId(), userAction, priority);
        }
        return notificationRepository.findAllByUserId(customService.currentUserId());
    }

    /**
     * Retriev notification by id
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
                orElse(new Notification(null, customService.currentUserHeadId(), task.getOrderId(), task.getScooterId()));

        if (notification.getId() == null) {
            notification = notificationRepository.save(notification);
        }
        return notification;

    }

    /**
     * Update Notification by provided user action
     *
     * @param notificationId the id for notification that has to be updated
     * @param userAction     Available values are one of {@code "rejected"} or {@code "approved"}
     * @return updated {@link Notification}
     */
    @Transactional
    public Notification updateNotification(@NonNull Long notificationId, @NonNull String userAction) {
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
            case "REJECTED" -> notification.getTask().setActive(); // reassign task
            case "APPROVED" -> // remove task; corresponding notification will be removed automatically.
                    taskRepository.deleteByOrderAndScooterIds(notification.getOrderId(), notification.getScooterId());
            default -> throw new NotAcceptableStatusException("Unrecognized user action");
        }
        // remove unnecessary notification
//        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteNotificationById(@NonNull Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteNotification(@NonNull Notification notification) {
        notificationRepository.delete(notification);
    }
}
