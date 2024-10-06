package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Scooter;
import com.izzy.payload.request.ScooterRequest;
import com.izzy.security.utils.Utils;
import com.izzy.service.ScooterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing Scooter-related operations.
 * Provides endpoints for creating, updating, and retrieving scooter information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/izzy/scooters")
public class ScooterController {
    private final ScooterService scooterService;

    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    /**
     * Retrieve scooters by filtering
     *
     * @param identifier   optional filtering parameter
     * @param batteryLevel optional filtering parameter
     * @param speedLimit   optional filtering parameter
     * @param status       optional filtering parameter
     * @param zoneName     optional filtering parameter
     * @return list of scooters
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Scooter> getScooters(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) String batteryLevel,
            @RequestParam(required = false) String speedLimit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String zoneName
    ) {
        try {
            return scooterService.getScootersByFiltering(identifier, batteryLevel, speedLimit, status, zoneName);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Retrieves a scooter by their ID.
     *
     * @param id the ID of the scooter to retrieve.
     * @return a ResponseEntity containing the scooter.
     * @throws ResourceNotFoundException if the scooter is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Scooter> getScooterById(@PathVariable Long id) {
        try {
            Scooter scooter = scooterService.getScooterById(id);
            if (scooter != null) {
                return ResponseEntity.ok(scooter);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Creates a new scooter.
     *
     * @param scooterRequestString the request payload containing scooter details.
     * @return a ResponseEntity containing a created scooter details.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Scooter> createScooter(@RequestBody String scooterRequestString) {
        try {
            // Validate request body
            ScooterRequest scooterRequest = (new ObjectMapper()).readValue(scooterRequestString, ScooterRequest.class);
            // processing
            Scooter scooter = scooterService.getScooterFromScooterRequest(scooterRequest, null);
            Scooter createdScooter = scooterService.createScooter(scooter);
            return ResponseEntity.ok(createdScooter);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Updates an existing scooter.
     *
     * @param id                   the ID of the scooter to update.
     * @param scooterRequestString the request payload containing updating scooter details.
     * @return ResponseEntity containing an updated scooter details.
     * @throws ResourceNotFoundException if the scooter is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Scooter> updateScooter(@PathVariable Long id, @RequestBody String scooterRequestString) {
        try {
            // Validate request body
            ScooterRequest scooterRequest = (new ObjectMapper()).readValue(scooterRequestString, ScooterRequest.class);
            // processing
            Scooter scooter = scooterService.getScooterFromScooterRequest(scooterRequest, id);
            Scooter updatedScooter = scooterService.updateScooter(id, scooter);
            if (updatedScooter != null) {
                return ResponseEntity.ok(updatedScooter);
            }
            throw new BadRequestException("cannot update.");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Deletes a scooter by their ID.
     *
     * @param id the ID of the scooter to delete.
     * @return ResponseEntity containing a success message.
     * @throws ResourceNotFoundException if the scooter is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteScooter(@PathVariable Long id) {
        if (scooterService.deleteScooter(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}