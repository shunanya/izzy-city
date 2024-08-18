package com.izzy.controllers;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Zone;
import com.izzy.service.ZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing zone-related operations.
 * Provides endpoints for creating, updating, and retrieving zone information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/zones")
public class ZoneController {
    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    /**
     * Retrieve all zone
     *
     * @return list of zone
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public List<Zone> getAllZones() {
        return zoneService.getAllZones();
    }

    /**
     * Retrieves a zone by their ID.
     *
     * @param id the ID of the zone to retrieve.
     * @return a ResponseEntity containing the zone.
     * @throws ResourceNotFoundException if the zone is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Manager','Supervisor')")
    public ResponseEntity<Zone> getZoneById(@PathVariable Long id) {
        Zone zone = zoneService.getZoneById(id);
        if (zone != null) {
            return ResponseEntity.ok(zone);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new zone.
     *
     * @param zoneName the creating zone name.
     * @return a ResponseEntity containing a created zone details.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Zone> createZone(@RequestBody String zoneName) {
        Zone createdZone = zoneService.createZone(zoneName);
        return ResponseEntity.ok(createdZone);
    }

    /**
     * Updates an existing zone.
     *
     * @param id                   the ID of the zone to update.
     * @param zoneName the updating zone name.
     * @return ResponseEntity containing an updated zone details.
     * @throws ResourceNotFoundException if the zone is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Zone> updateZone(@PathVariable Long id, @RequestBody String zoneName) {
        Zone updatedZone = zoneService.updateZone(id, zoneName);
        if (updatedZone != null) {
            return ResponseEntity.ok(updatedZone);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a zone by their ID.
     *
     * @param id the ID of the zone to delete.
     * @return ResponseEntity containing a success message.
     * @throws ResourceNotFoundException if the zone is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        if (zoneService.deleteZone(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
