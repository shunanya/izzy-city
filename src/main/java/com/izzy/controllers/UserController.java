package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.utils.Utils;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.service.UserPrincipal;
import com.izzy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/izzy/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<User> getUsers(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName, @RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String gender, @RequestParam(required = false) String zone, @RequestParam(required = false) String shift) {
        try {
            return userService.getUsers(firstName, lastName, phoneNumber, gender, zone, shift);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    private boolean checkAllowability(@NonNull User requsringUser) {
        Map<String, Integer> roles = new HashMap<>();
        roles.put("ROLE_Admin", 1);
        roles.put("ROLE_Manager", 2);
        roles.put("ROLE_Supervisor", 3);
        roles.put("ROLE_Charger", 4);
        roles.put("ROLE_Scout", 5);
        // Detects requesting role
        UserPrincipal requestingUserDetails = UserPrincipal.build(requsringUser);
        GrantedAuthority[] requestingAuth = requestingUserDetails.getAuthorities().toArray(new GrantedAuthority[0]);
        int requestingRole = roles.size();
        for (GrantedAuthority ra : requestingAuth) {
            String auth = ra.getAuthority();
            if (auth != null) {
                Integer i = roles.get(auth);
                if (i != null && i < requestingRole) requestingRole = i;
            }
        }
        // Detects current user role
        GrantedAuthority[] currentAuth = new GrantedAuthority[0];
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            currentAuth = currentUserDetails.getAuthorities().toArray(new GrantedAuthority[0]);
        }
        int currentRole = roles.size();
        for (GrantedAuthority ra : currentAuth) {
            String auth = ra.getAuthority();
            if (auth != null) {
                Integer i = roles.get(auth);
                if (i != null && i < currentRole) currentRole = i;
            }
        }
        // check allowability
        return currentRole <= requestingRole;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<?> getUserById(@P("id") @PathVariable Long id, @RequestParam(value = "short", required = false, defaultValue = "false") boolean shortView) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                if (checkAllowability(user))
                    return ResponseEntity.ok(shortView ? userService.convertUserToShort(user) : user);
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
            User user = userService.getUserFromUserRequest(userRequest, true);
            if (checkAllowability(user)) user = userService.saveUser(user);
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
            User user = userService.getUserFromUserRequest(userRequest, false);
            if (checkAllowability(user)) user = userService.updateUser(id, user);
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
                if (checkAllowability(userService.getUserById(id))) {
                    if (userService.deleteUser(id)) {
                        return ResponseEntity.ok(new MessageResponse("User deleted"));
                    } else throw new UnknownServiceException("Couldn't delete user with id="+id);
                } else throw new AccessDeniedException("not allowed to delete user with above your role");
            } else throw new ResourceNotFoundException("User", "id", id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}