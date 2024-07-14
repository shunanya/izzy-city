package com.izzy.service;

import com.izzy.model.ScooterEntity;
import com.izzy.repository.ScooterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScooterService {
    @Autowired
    private ScooterRepository scooterRepository;

    public List<ScooterEntity> getAllScooters() {
        return scooterRepository.findAll();
    }

    public ScooterEntity getScooterById(Long id) {
        return scooterRepository.findById(id).orElse(null);
    }

    public ScooterEntity createScooter(ScooterEntity scooter) {
        return scooterRepository.save(scooter);
    }

    public ScooterEntity updateScooter(Long id, ScooterEntity scooter) {
        return scooterRepository.findById(id).map(existingScooter -> {
            existingScooter.setIdentifier(scooter.getIdentifier());
            existingScooter.setStatus(scooter.getStatus());
            existingScooter.setBatteryLevel(scooter.getBatteryLevel());
            existingScooter.setZone(scooter.getZone());
            existingScooter.setSpeedLimit(scooter.getSpeedLimit());
            return scooterRepository.save(existingScooter);
        }).orElse(null);
    }

    public boolean deleteScooter(Long id) {
        return scooterRepository.findById(id).map(scooter -> {
            scooterRepository.delete(scooter);
            return true;
        }).orElse(false);
    }
}