package com.izzy.controllers;

import com.izzy.exception.utils.Utils;
import com.izzy.model.User;
import com.izzy.payload.response.MessageResponse;
import com.izzy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse(400, Utils.substringFromException(e)));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}