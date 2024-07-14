package com.izzy.controllers;

import com.izzy.model.ZoneEntity;
import com.izzy.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zones")
public class ZoneController {
    @Autowired
    private ZoneService zoneService;

    @GetMapping
    public List<ZoneEntity> getAllZones() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneEntity> getZoneById(@PathVariable Long id) {
        ZoneEntity zone = zoneService.getZoneById(id);
        if (zone != null) {
            return ResponseEntity.ok(zone);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ZoneEntity> createZone(@RequestBody ZoneEntity zone) {
        ZoneEntity createdZone = zoneService.createZone(zone);
        return ResponseEntity.ok(createdZone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneEntity> updateZone(@PathVariable Long id, @RequestBody ZoneEntity zone) {
        ZoneEntity updatedZone = zoneService.updateZone(id, zone);
        if (updatedZone != null) {
            return ResponseEntity.ok(updatedZone);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        if (zoneService.deleteZone(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
