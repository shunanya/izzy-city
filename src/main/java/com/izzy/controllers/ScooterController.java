package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.BadRequestException;
import com.izzy.security.utils.Utils;
import com.izzy.model.Scooter;
import com.izzy.payload.request.ScooterRequest;
import com.izzy.service.ScooterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/izzy/scooters")
public class ScooterController {
    private final ScooterService scooterService;

    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @GetMapping
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor') or hasRole('Charger') or hasRole('Scout')")
    public List<Scooter> getAllScooters() {
        return scooterService.getAllScooters();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Scooter> getScooterById(@PathVariable Long id) {
        Scooter scooter = scooterService.getScooterById(id);
        if (scooter != null) {
            return ResponseEntity.ok(scooter);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
//    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Scooter> createScooter(@RequestBody String scooterRequestString) {
        try {
            // Validate request body
            ScooterRequest scooterRequest = (new ObjectMapper()).readValue(scooterRequestString, ScooterRequest.class);
            // processing
            Scooter scooter = scooterService.getScooterFromScooterRequest(scooterRequest, true);
            Scooter createdScooter = scooterService.createScooter(scooter);
            return ResponseEntity.ok(createdScooter);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Scooter> updateScooter(@PathVariable Long id, @RequestBody String scooterRequestString) {
        try {
            // Validate request body
            ScooterRequest scooterRequest = (new ObjectMapper()).readValue(scooterRequestString, ScooterRequest.class);
            // processing
            Scooter scooter = scooterService.getScooterFromScooterRequest(scooterRequest, false);
            Scooter updatedScooter = scooterService.updateScooter(id, scooter);
            if (updatedScooter != null) {
                return ResponseEntity.ok(updatedScooter);
            }
            throw new BadRequestException("Error: cannot update.");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteScooter(@PathVariable Long id) {
        if (scooterService.deleteScooter(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}