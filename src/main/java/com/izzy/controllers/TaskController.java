package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.Task;
import com.izzy.model.TaskDTO;
import com.izzy.payload.response.ApiResponse;
import com.izzy.security.utils.Utils;
import com.izzy.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing task-related operations.
 * Provides endpoints for creating, updating, and retrieving task information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/izzy/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Retrieve all tasks included into order
     *
     * @param viewType   optional parameter to get 'simple'(aka origin), 'short' and 'detailed' task data view (default is 'simple')
     * @param orderId    optional owner-order id of the tasks
     * @param scooterId  optional scooter ID associated with the task
     * @param priorities optional priorities of task (concrete data or range of data)
     * @param status     optional status of task to be retrieving
     * @return list of filtered tasks
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<?> getTasks(@RequestParam(name = "view", required = false, defaultValue = "simple") String viewType,
                            @RequestParam(required = false) Long orderId,
                            @RequestParam(required = false) Long scooterId,
                            @RequestParam(required = false) String priorities,
                            @RequestParam(required = false) String status) {
        try {
            return taskService.getTasksByFiltering(viewType, orderId, scooterId, priorities, status);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieve all tasks assigned to current user
     *
     * @param viewType optional parameter to get 'simple', 'short' and 'detailed' task data view (default is 'simple')
     * @return list of tasks
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('Charger','Scout')")
    public List<?> getTasksAssignedMe(@RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
        try {
            switch (viewType) {
                case "simple" -> {
                    return taskService.getTasksAssignedMe();
                }
                case "short" -> {
                    return taskService.getShortTaskInfosAssignedMe();
                }
                case "detailed" -> {
                    return taskService.getDetailedTaskInfosAssignedMe();
                }
                default ->
                        throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieve all tasks assigned to user
     *
     * @param userId   user ID who assigned any task
     * @param viewType optional parameter to get 'simple', 'short' and 'detailed' task data view (default is 'simple')
     * @return list of tasks
     * @throws ResourceNotFoundException if the user is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/assigned/{userId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<?> getTasksByAssigned(@PathVariable Long userId,
                                      @RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
        try {
            switch (viewType) {
                case "simple" -> {
                    return taskService.getTasksByAssigned(userId);
                }
                case "short" -> {
                    return taskService.getShortTaskInfosByAssigned(userId);
                }
                case "detailed" -> {
                    return taskService.getDetailedTaskInfosByAssigned(userId);
                }
                default ->
                        throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Append a new task to the existing set of tasks
     *
     * @param taskRequestString task details to be appended
     * <p>
     *  the task should contain {@code orderId}, {@code scooterId} and optional {@code priority}
     * </p>
     * @return updated list of tasks
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Task> appendTaskToOrder(@RequestBody String taskRequestString) {
        try {
            // Validate request body
            TaskDTO taskDTO = (new ObjectMapper()).readValue(taskRequestString, TaskDTO.class);
            return taskService.appendTask(taskDTO);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Remove task from existing order tasks
     *
     * @param taskRequestString task details to be removed
     *                          <p>
     *                          task should contain {@code orderId} and {@code scooterId}
     *                          </p>
     * @return nothing
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> deleteTaskFromOrder(@RequestBody String taskRequestString) {
        try {
            // Validate request body
            TaskDTO taskDTO = (new ObjectMapper()).readValue(taskRequestString, TaskDTO.class);
            // Processing
            taskService.removeTask(taskDTO);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, String.format("Task deleted {orderId: %s, scooterId: %s}", taskDTO.getOrderId(), taskDTO.getScooterId())));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Change status of task to completed or canceled
     *
     * @param taskRequestString task details to be updated
     *                          <p>
     *                          The task must include {@code orderId}, {@code scooterId},
     *                          {@code status} (with allowable values of 'completed' or 'canceled'),
     *                          and {@code comment} to explain the status set.
     *                          </p>
     * @return ResponseEntity containing a success message
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PatchMapping
    public ResponseEntity<?> markTaskAsCompletedOrCanceled(@RequestBody String taskRequestString) {
        try {
            // Validate request body
            TaskDTO taskDTO = (new ObjectMapper()).readValue(taskRequestString, TaskDTO.class);
            // Processing
            List<Task> tasks = taskService.markTaskAsCompletedOrCanceled(taskDTO);
            return tasks.isEmpty() ?
                    ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST, "Something went wrong.")) :
                    ResponseEntity.ok(new ApiResponse(HttpStatus.OK, String.format("Task %s marked as completed.", taskRequestString.replaceAll("\\s", ""))));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}
