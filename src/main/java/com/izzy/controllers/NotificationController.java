package com.izzy.controllers;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Notification;
import com.izzy.security.utils.Utils;
import com.izzy.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/izzy/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

//    /**
//     * Retrieve Tasks for userManager that currently signed-in
//     *
//     * @param doneTasksOnly optional boolean parameter that define to get only Complete and Canceled tasks
//     * @param viewType      optional parameter to get 'simple', 'short' and 'detailed' user data view (default is 'simple')
//     * @return List of tasks {@link com.izzy.model.Task}
//     */
//    @GetMapping("tasks")
//    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
//    public List<?> getAllNotificationsForCurrentUser(
//            @RequestParam(name = "doneTasksOnly", required = false, defaultValue = "true") Boolean doneTasksOnly,
//            @RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
//        try {
//            switch (viewType) {
//                case "simple" -> {
//                    return notificationService.getAllNotificationsForCurrentUserManager(doneTasksOnly, null);
//                }
//                case "short" -> {
//                    return notificationService.getAllNotificationsForCurrentUserManager(doneTasksOnly, true);
//                }
//                case "detailed" -> {
//                    return notificationService.getAllNotificationsForCurrentUserManager(doneTasksOnly, false);
//                }
//                default ->
//                        throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
//            }
//        } catch (Exception ex) {
//            throw new BadRequestException(Utils.substringErrorFromException(ex));
//        }
//    }
//
//    /**
//     * Retrieve tasks assigned to currently signed-in user
//     * <p>
//     * The notification can be assigned for user manager or executor.
//     *     <ul>
//     *     <li>User Manager receive notification when executor set task as CANCELED or COMPLETED
//     *     <li>Executor receive notification when User Manager set soma action (APPROVED or REJECTED)
//     *     </ul>
//     * </p>
//     *
//     * @param status   optional statuses that should be retrieved
//     * @param viewType optional parameter to get 'simple', 'short' and 'detailed' user data view (default is 'simple')
//     * @return List of requesting task {@link com.izzy.model.Task}
//     */
//    @GetMapping("tasks/assigned")
//    public List<?> GetTaskNotificationsForCurrentUser(
//            @RequestParam(required = false) List<String> status,
//            @RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
//        try {
//            switch (viewType) {
//                case "simple" -> {
//                    return notificationService.getFilteredTaskNotificationsForCurrentUser(status, null);
//                }
//                case "short" -> {
//                    return notificationService.getFilteredTaskNotificationsForCurrentUser(status, true);
//                }
//                case "detailed" -> {
//                    return notificationService.getFilteredTaskNotificationsForCurrentUser(status, false);
//                }
//                default ->
//                        throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
//            }
////            return new ArrayList<>();
//        } catch (Exception ex) {
//            throw new BadRequestException(Utils.substringErrorFromException(ex));
//        }
//    }

    /**
     * Retrieve Notifications for userManager that currently signed-in
     *
     * @param userAction optional user-manager action
     * @param status optional Task status that should be retrieved
     * @return List notifications {@link com.izzy.model.Notification}
     */
    @GetMapping()
    public List<?> GetNotificationsForCurrentUser(
            @RequestParam(name = "action", required = false) String userAction,
            @RequestParam(name = "status", required = false) String status) {
        try {
            return notificationService.getNotificationsForCurrentUser(userAction, status);
        } catch (Exception ex) {
            throw new BadRequestException(Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieve Notification by id
     * @param notificationId the id for retrieving notification
     * @return {@link Notification}
     */
    @GetMapping("{notificationId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public Notification GetNotificationById(@PathVariable Long notificationId){
        try{
            return notificationService.getNotificationByID(notificationId);
        } catch (Exception ex) {
            throw new BadRequestException(Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Update notification
     *
     * @param notificationId the id for updated notification
     * @param userAction     Available values are one of <code>"rejected"</code> or <code>"approved"</code>
     * @return the updated notification
     */
    @PutMapping("{notificationId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public Notification updateNotification(@PathVariable Long notificationId, @RequestParam(name = "action") String userAction) {
        try {
            return notificationService.updateNotification(notificationId, userAction);
        } catch (Exception ex) {
            throw new BadRequestException(Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Handle the situation in case the executor is already familiar with the manager reaction.
     *
     * @param notificationId the id of looked through notification
     */
    @PatchMapping("{notificationId}")
//    @PreAuthorize("hasAnyRole('Charger','Scout')")
    public void actionHandling(@PathVariable Long notificationId){
        try{
            notificationService.actionHandling(notificationId);
        } catch (Exception ex) {
            throw new BadRequestException(Utils.substringErrorFromException(ex));
        }
    }

    @DeleteMapping("{notificationId}")
    @PreAuthorize("hasRole('Admin')")
    public void deleteNotification(@PathVariable Long notificationId){
        try{
            notificationService.deleteNotification(notificationId);
        }catch (Exception ex) {
            throw new BadRequestException(Utils.substringErrorFromException(ex));
        }
    }

}
