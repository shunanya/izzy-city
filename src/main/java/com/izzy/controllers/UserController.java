package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.utils.Utils;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/izzy/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')") //?????????????????
    public List<User> getUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String gender,
//            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) String shift) {
        return userService.getUsers(firstName, lastName, phoneNumber, gender, shift);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<?> createUser(@Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User user = userService.getUserFromUserRequest(userRequest, true);
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(400, Utils.substringErrorFromException(e)));
        }
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User rawUser = userService.getUserFromUserRequest(userRequest, false);
            User updatedUser = userService.updateUser(id, rawUser);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(400, Utils.substringErrorFromException(e)));
        }
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (userService.deleteUser(id)) {
                return ResponseEntity.ok(new MessageResponse("User deleted"));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(400, Utils.substringErrorFromException(e)));
        }
    }
}