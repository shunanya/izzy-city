package com.izzy.controllers;

import com.izzy.model.Scooter;
import com.izzy.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scooters")
public class ScooterController {
    @Autowired
    private ScooterService scooterService;

    @GetMapping
    public List<Scooter> getAllScooters() {
        return scooterService.getAllScooters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scooter> getScooterById(@PathVariable Long id) {
        Scooter scooter = scooterService.getScooterById(id);
        if (scooter != null) {
            return ResponseEntity.ok(scooter);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Scooter> createScooter(@RequestBody Scooter scooter) {
        Scooter createdScooter = scooterService.createScooter(scooter);
        return ResponseEntity.ok(createdScooter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Scooter> updateScooter(@PathVariable Long id, @RequestBody Scooter scooter) {
        Scooter updatedScooter = scooterService.updateScooter(id, scooter);
        if (updatedScooter != null) {
            return ResponseEntity.ok(updatedScooter);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScooter(@PathVariable Long id) {
        if (scooterService.deleteScooter(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}