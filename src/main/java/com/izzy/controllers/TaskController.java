package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.model.misk.Task;
import com.izzy.security.utils.Utils;
import com.izzy.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/izzy/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Task> getTasksByOrderId(@PathVariable Long orderId) {
        try {
            return taskService.getTasksByOrderId(orderId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('Charger','Scout')")
    public List<Task> getTasksAssignedMe() {
        try {
            return taskService.getTasksAssignedMe();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @GetMapping("/assigned/{userId}")
    public List<Task> getTasksByAssigned(@PathVariable Long userId) {
        try {
            return taskService.getTasksByAssigned(userId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

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

    @PatchMapping("/complete/{orderId}")
    public List<Task> markTaskAsCompleted(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            return taskService.markTaskAsCompleted(orderId, task);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PatchMapping("/cancel/{orderId}")
    public List<Task> markTaskAsCanceled(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            return taskService.markTaskAsCanceled(orderId, task);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}
