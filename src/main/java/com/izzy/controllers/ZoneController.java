package com.izzy.controllers;

import com.izzy.model.Zone;
import com.izzy.service.ZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zones")
public class ZoneController {
    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public List<Zone> getAllZones() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin') or hasRole('Manager') or hasRole('Supervisor')")
    public ResponseEntity<Zone> getZoneById(@PathVariable Long id) {
        Zone zone = zoneService.getZoneById(id);
        if (zone != null) {
            return ResponseEntity.ok(zone);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Zone> createZone(@RequestBody Zone zone) {
        Zone createdZone = zoneService.createZone(zone);
        return ResponseEntity.ok(createdZone);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Zone> updateZone(@PathVariable Long id, @RequestBody Zone zone) {
        Zone updatedZone = zoneService.updateZone(id, zone);
        if (updatedZone != null) {
            return ResponseEntity.ok(updatedZone);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        if (zoneService.deleteZone(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
