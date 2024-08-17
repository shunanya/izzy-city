package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@RestController
@RequestMapping("/izzy/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

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
    public ResponseEntity<?> markTaskAsCompleted(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            List<Task> tasks = taskService.markTaskAsCompleted(orderId, task);
            return tasks.isEmpty()?
                    ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Something went wrong.")):
                    ResponseEntity.ok( new ApiResponse(HttpStatus.OK.value(), String.format("Task '%s' marked as completed.", taskRequestString)));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<?> markTaskAsCanceled(@PathVariable Long orderId, @RequestBody String taskRequestString) {
        try {
            // Validate request body
            Task task = (new ObjectMapper()).readValue(taskRequestString, Task.class);
            // Processing
            List<Task> tasks = taskService.markTaskAsCanceled(orderId, task);
            return tasks.isEmpty()?
                    ResponseEntity.badRequest().body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Something went wrong.")):
                    ResponseEntity.ok( new ApiResponse(HttpStatus.OK.value(), String.format("Task '%s' marked as canceled.", taskRequestString)));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}
