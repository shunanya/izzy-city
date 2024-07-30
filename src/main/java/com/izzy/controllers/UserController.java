package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.utils.Utils;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.security.custom.service.CustomService;
import com.izzy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.UnknownServiceException;
import java.util.List;

@RestController
@RequestMapping("/izzy/users")
public class UserController {
    private final UserService userService;
    private final CustomService customService;

    public UserController(UserService userService, CustomService customService) {
        this.userService = userService;
        this.customService = customService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<User> getUsers(@RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String gender,
                               @RequestParam(required = false) String zone,
                               @RequestParam(required = false) String shift) {
        try {
            return userService.getUsers(firstName, lastName, phoneNumber, gender, zone, shift);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> getUserById(@P("id") @PathVariable Long id,
                                         @RequestParam(value = "short", required = false, defaultValue = "false") boolean shortView) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                if (customService.checkAllowability(user))
                    return ResponseEntity.ok(shortView ? userService.convertUserToShort(user) : userService.connvertUserToUserInfo(user));
                else
                    throw new AccessDeniedException("not allowed to request user with above your role");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<User> createUser(@Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User user = userService.getUserFromUserRequest(null, userRequest);
            if (customService.checkAllowability(user)) user = userService.saveUser(user);
            else throw new AccessDeniedException("not allowed to create user with above your role");

            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User user = userService.getUserFromUserRequest(id, userRequest);
            if (customService.checkAllowability(user)) user = userService.updateUser(id, user);
            else throw new AccessDeniedException("not allowed to update user with above your role");
            if (user != null) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (userService.existsUser(id)) {
                if (customService.checkAllowability(userService.getUserById(id))) {
                    if (userService.deleteUser(id)) {
                        return ResponseEntity.ok(new MessageResponse("User deleted"));
                    } else throw new UnknownServiceException("Couldn't delete user with id=" + id);
                } else throw new AccessDeniedException("not allowed to delete user with above your role");
            } else throw new ResourceNotFoundException("User", "id", id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}