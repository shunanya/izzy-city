package com.izzy.controllers;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Notification;
import com.izzy.security.utils.Utils;
import com.izzy.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/izzy/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Retrieve Notifications for userManager that currently signed in
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
     * Update notification by adding user-manager reaction
     *
     * @param notificationId the id for updated notification
     * @param userAction     Available values are one of {@code "rejected"} or {@code "approved"}
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
     * <p>Ths request marks Notification As Read and does the following actions
     * <ul>
     *     <li>remove Task and Notification if manager approved executor action
     *     <li>reassign Task to executor and remove Notification if manager rejected executor action
     * </ul>
     * </p>
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
            notificationService.deleteNotificationById(notificationId);
        }catch (Exception ex) {
            throw new BadRequestException(Utils.substringErrorFromException(ex));
        }
    }

}
