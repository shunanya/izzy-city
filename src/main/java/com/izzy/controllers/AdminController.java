package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.utils.Utils;
import com.izzy.model.User;
import com.izzy.payload.request.SignupRequest;
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

@RestController
@RequestMapping("/izzy/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> registerUser(@RequestBody String signupRequestString) {
        try {
            // Validate request body (in correspondence to SignupRequest class)
            SignupRequest signupRequest = (new ObjectMapper()).readValue(signupRequestString, SignupRequest.class);
            // Processing
             if (adminService.existByUserIdentifier(signupRequest.getPhoneNumber())) {
                throw new BadCredentialsException("Error: phone number is already in use!");
            }
            User savedUser = adminService.registerUser(signupRequest);
            return ResponseEntity.ok(savedUser);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

}
