package com.izzy.controllers;

import com.izzy.model.ScooterEntity;
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
    public List<ScooterEntity> getAllScooters() {
        return scooterService.getAllScooters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScooterEntity> getScooterById(@PathVariable Long id) {
        ScooterEntity scooter = scooterService.getScooterById(id);
        if (scooter != null) {
            return ResponseEntity.ok(scooter);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ScooterEntity> createScooter(@RequestBody ScooterEntity scooter) {
        ScooterEntity createdScooter = scooterService.createScooter(scooter);
        return ResponseEntity.ok(createdScooter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScooterEntity> updateScooter(@PathVariable Long id, @RequestBody ScooterEntity scooter) {
        ScooterEntity updatedScooter = scooterService.updateScooter(id, scooter);
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