package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.UserInfo;
import com.izzy.security.utils.Utils;
import com.izzy.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * controller for managing the operations allowed to the administrator only.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/izzy/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Create and register a new user
     *
     * @param userRequestString the creating user details
     * @return ResponseEntity containing a registered user details
     * @throws BadCredentialsException if provided phone number is already in use
     * @throws AccessDeniedException   if operation is not permitted for current user
     */
    @PostMapping("/signup")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> registerUser(@RequestBody String userRequestString) {
        try {
            // Validate request body (in correspondence to UserRequest class)
            UserRequest userRequest = (new ObjectMapper()).readValue(userRequestString, UserRequest.class);
            // Processing
            if (adminService.existByUserIdentifier(userRequest.getPhoneNumber())) {
                throw new BadCredentialsException("Error: phone number is already in use!");
            }
            User savedUser = adminService.registerUser(userRequest);
            return ResponseEntity.ok(new UserInfo(savedUser, null, false));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

}
