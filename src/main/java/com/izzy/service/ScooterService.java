package com.izzy.service;

import com.izzy.model.Scooter;
import com.izzy.repository.ScooterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScooterService {
    private final ScooterRepository scooterRepository;

    public ScooterService(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    public List<Scooter> getAllScooters() {
        return scooterRepository.findAll();
    }

    public Scooter getScooterById(Long id) {
        return scooterRepository.findById(id).orElse(null);
    }

    public Scooter createScooter(Scooter scooter) {
        return scooterRepository.save(scooter);
    }

    public Scooter updateScooter(Long id, Scooter scooter) {
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