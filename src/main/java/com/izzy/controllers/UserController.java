package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.security.custom.service.CustomService;
import com.izzy.security.utils.Utils;
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

/**
 * Controller for managing user-related operations.
 * Provides endpoints for creating, updating, and retrieving user information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/izzy/users")
public class UserController {
    private final UserService userService;
    private final CustomService customService;

    /**
     * Constructor for UserController.
     *
     * @param userService   the service to manage user operations.
     * @param customService the service to handle custom operations.
     */
    public UserController(UserService userService, CustomService customService) {
        this.userService = userService;
        this.customService = customService;
    }

    /**
     * Retrieves a list of users with filtering.
     *
     * @param shortView   optional parameter to get minimal user data if True (default is False)
     * @param firstName   optional filtering parameter
     * @param lastName    optional filtering parameter
     * @param phoneNumber optional filtering parameter
     * @param gender      optional filtering parameter
     * @param zone        optional filtering parameter
     * @param shift       optional filtering parameter
     * @param roles       optional filtering parameter
     * @return list of users.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<?> getUsers(
            @RequestParam(name = "short", required = false, defaultValue = "false") boolean shortView,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String roles) {
        try {
            return userService.getUsers(shortView, firstName, lastName, phoneNumber, gender, zone, shift, roles);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id        the ID of the user to retrieve.
     * @param shortView optional parameter to get minimal user data if True (default is False)
     * @return a ResponseEntity containing the user.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> getUserById(@P("id") @PathVariable Long id,
                                         @RequestParam(name = "short", required = false, defaultValue = "false") boolean shortView) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                if (customService.checkAllowability(user))
                    return ResponseEntity.ok(shortView ? userService.convertUserToShort(user) : user);
                else
                    throw new AccessDeniedException("not allowed to request user with above your role");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Creates a new user.
     *
     * @param userRequestString the request payload containing user details.
     * @return a ResponseEntity containing a created user details.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<User> createUser(@Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User user = userService.getUserFromUserRequest(null, userRequest);
            if (customService.checkAllowability(user))
                user = userService.saveUser(user);
            else
                throw new AccessDeniedException("not allowed to create user with above your role");

            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Updates an existing user.
     *
     * @param id                the ID of the user to update.
     * @param userRequestString the request payload containing updated user details.
     * @return ResponseEntity containing an updated user details.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User user = userService.getUserFromUserRequest(id, userRequest);
            if (customService.checkAllowability(user, true))
                user = userService.updateUser(id, user);
            else
                throw new AccessDeniedException("not allowed to update user with above your role");
            if (user != null) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @return ResponseEntity containing a success message.
     * @throws ResourceNotFoundException if the user is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     * @throws UnknownServiceException   if operation cannot be fulfilled by unknown reason
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (userService.existsUser(id)) {
                if (customService.checkAllowability(userService.getUserById(id))) {
                    if (userService.deleteUser(id)) {
                        return ResponseEntity.ok(new MessageResponse("User deleted"));
                    } else
                        throw new UnknownServiceException("Couldn't delete user with id=" + id);
                } else
                    throw new AccessDeniedException("not allowed to delete user with above your role");
            } else
                throw new ResourceNotFoundException("User", "id", id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}