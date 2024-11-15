package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.security.utils.Utils;
import com.izzy.service.RoleService;
import com.izzy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing user-related operations.
 * Provides endpoints for creating, updating, and retrieving user information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/izzy/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService   the service to manage user operations.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a list of users with filtering.
     *
     * @param viewType    optional parameter to get 'simple', 'short' and 'detailed' user data view (default is 'simple')
     * @param firstName   optional filtering parameter
     * @param lastName    optional filtering parameter
     * @param phoneNumber optional filtering parameter
     * @param gender      optional filtering parameter
     * @param dateOfBirth optional filtering parameter
     * @param shift       optional filtering parameter
     * @param createdAt   optional filtering parameter
     * @param zoneName    optional filtering parameter
     * @param roles       optional filtering parameter (for detail see {@link  RoleService#getRolesFromParam getRolesFromParam} method definitions
     * @return list of users.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<?> getUsers(
            @RequestParam(name = "view", required = false, defaultValue = "simple") String viewType,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String createdAt,
            @RequestParam(required = false) String zoneName,
            @RequestParam(required = false) String roles) {
        try {
            return userService.getUsers(viewType, firstName, lastName, phoneNumber, gender, dateOfBirth, shift, createdAt, zoneName, roles);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id       the ID of the user to retrieve.
     * @param viewType optional parameter that defined a presenting style for user data ("simple","short","detailed")
     * @return a ResponseEntity containing the user data.
     * @throws UnrecognizedPropertyException if property is not recognized.
     * @throws AccessDeniedException         if operation is not permitted for current user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> getUserById(@P("id") @PathVariable Long id,
                                         @RequestParam(name = "view", required = false, defaultValue = "simple") String viewType) {
        try {
            User user = userService.getUserById(id);
            switch (viewType) {
                case "simple" -> {
                    return ResponseEntity.ok(user);
                }
                case "short" -> {
                    return ResponseEntity.ok(userService.connvertUserToUserInfo(user, true));
                }
                case "detailed" -> {
                    return ResponseEntity.ok(userService.connvertUserToUserInfo(user, false));
                }
                default ->
                        throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Creates a new user.
     *
     * @param userRequestString the request payload (json string) containing user details.
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
            User user = userService.saveUser(userService.getUserFromUserRequest(null, userRequest));
            String msg = Utils.appendKeyValuePairIntoJSONString(userRequestString, "id", user.getId());
            userService.addUserHistory("create", msg);
            logger.info("User created: {}", msg);
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
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
//    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody String userRequestString) {
        try {
            // Validate request body
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // processing
            User user = userService.updateUser(id, userService.getUserFromUserRequest(id, userRequest));
            if (user != null) {
                String msg = Utils.appendKeyValuePairIntoJSONString(userRequestString, "id", id);
                logger.info("User updated: {}", msg);
                userService.addUserHistory("update", msg);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @return ResponseEntity containing a success message.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            logger.info("User deleted: {}", id);
            userService.addUserHistory("delete", Utils.appendKeyValuePairIntoJSONString(null, "id", id));
            return ResponseEntity.ok(new MessageResponse("User deleted"));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}