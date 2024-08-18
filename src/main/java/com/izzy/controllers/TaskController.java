package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.misk.Task;
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
     * @param orderId owner-order id of the tasks
     * @param viewType optional parameter to get 'simple', 'short' and 'detailed' task data view (default is 'simple')
     * @return list of tasks
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<?> getTasksByOrderId(@PathVariable Long orderId,
                                     @RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
        try {
            switch (viewType){
                case "simple" -> {return taskService.getTasksByOrderId(orderId);}
                case "short" -> {return taskService.getShortTaskInfosByOrderId(orderId);}
                case "detailed" -> {return taskService.getDetailedTaskInfosByOrderId(orderId);}
                default -> throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
            }

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieve all tasks assigned to current user
     *
     * @param viewType optional parameter to get 'simple', 'short' and 'detailed' task data view (default is 'simple')
     * @return list of tasks
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('Charger','Scout')")
    public List<?> getTasksAssignedMe(@RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
        try {
            switch (viewType){
                case "simple" -> {return taskService.getTasksAssignedMe();}
                case "short" -> {return taskService.getShortTaskInfosAssignedMe();}
                case "detailed" -> {return taskService.getDetailedTaskInfosAssignedMe();}
                default -> throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieve all tasks assigned to user
     *
     * @param userId user ID whom assigned any task
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
            switch (viewType){
                case "simple" -> {return taskService.getTasksByAssigned(userId);}
                case "short" -> {return taskService.getShortTaskInfosByAssigned(userId);}
                case "detailed" -> {return taskService.getDetailedTaskInfosByAssigned(userId);}
                default -> throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Appent a new task to the existing tasks
     *
     * @param orderId owner-order id of the tasks
     * @param taskRequestString task details to be appended
     * @return updated list of tasks
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Task> appendTaskToOrder(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            return taskService.appendTask(orderId, task);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Remove task from existing tasks
     *
     * @param orderId owner-order id of the tasks
     * @param taskRequestString task details to be removed
     * @return nothing
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Void> deleteTaskFromOrder(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            taskService.removeTask(orderId, task);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Change status of task to completed
     *
     * @param orderId owner-order id of the tasks
     * @param taskRequestString task details to be updated
     * @return ResponseEntity containing a success message
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PatchMapping("/complete/{orderId}")
    public ResponseEntity<?> markTaskAsCompleted(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            List<Task> tasks = taskService.markTaskAsCompleted(orderId, task);
            return tasks.isEmpty()?
                    ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Something went wrong.")):
                    ResponseEntity.ok( new ApiResponse(HttpStatus.OK.value(), String.format("Task %s marked as completed.", taskRequestString.replaceAll("\\s", ""))));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Change status of task to canceled
     *
     * @param orderId owner-order id of the tasks
     * @param taskRequestString task details to be updated
     * @return ResponseEntity containing a success message
     * @throws ResourceNotFoundException if the order is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<?> markTaskAsCanceled(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            List<Task> tasks = taskService.markTaskAsCanceled(orderId, task);
            return tasks.isEmpty()?
                    ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Something went wrong.")):
                    ResponseEntity.ok( new ApiResponse(HttpStatus.OK.value(), String.format("Task '%s' marked as canceled.", taskRequestString.replaceAll("\\s", ""))));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}
