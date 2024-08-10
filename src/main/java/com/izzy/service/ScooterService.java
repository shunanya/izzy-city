package com.izzy.service;

import com.izzy.model.Scooter;
import com.izzy.model.Zone;
import com.izzy.payload.request.ScooterRequest;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.ZoneRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ScooterService {
    private final ScooterRepository scooterRepository;
    private final ZoneRepository zoneRepository;

    public ScooterService(ScooterRepository scooterRepository, ZoneRepository zoneRepository) {
        this.scooterRepository = scooterRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<Scooter> getAllScooters() {
        return scooterRepository.findAll();
    }

    public Scooter getScooterById(Long id) {
        return scooterRepository.findById(id).orElse(null);
    }

    public Scooter getScooterFromScooterRequest(@NonNull ScooterRequest scooterRequest, @Nullable Long scooterId) {
        boolean creation =(scooterId == null);

        Scooter scooter = new Scooter();
        if (!creation) scooter.setId(scooterId);
        String tmp = scooterRequest.getIdentifier();
        if (tmp != null && !tmp.isBlank()) scooter.setIdentifier(tmp);
        tmp = scooterRequest.getStatus();
        if (tmp != null && !tmp.isBlank()) {
            if (ScooterRequest.Status.checkByValue(tmp)) scooter.setStatus(tmp);
            else throw new IllegalArgumentException(String.format("status field contains illegal value '%s'", tmp));
        }
        Integer i = scooterRequest.getBatteryLevel();
        if (i != null) scooter.setBatteryLevel(i);
        i = scooterRequest.getSpeedLimit();
        if (i != null) scooter.setSpeedLimit(i);
        tmp = scooterRequest.getZone();
        if (tmp != null && !tmp.isBlank()) {
            Optional<Zone> existingZone = zoneRepository.findByName(tmp);
            if (existingZone.isPresent()) scooter.setZone(existingZone.get());
            else throw new IllegalArgumentException(String.format("Error: Provided zone named '%s' not found", tmp));
        }
        return scooter;
    }

    @Transactional
    public Scooter createScooter(Scooter scooter) {
        return scooterRepository.save(scooter);
    }

    @Transactional
    public Scooter updateScooter(@NonNull Long id, @NonNull Scooter scooter) {
        return scooterRepository.findById(id).map(existingScooter -> {
            String tmp = scooter.getIdentifier();
            if (tmp != null && !tmp.isBlank()) existingScooter.setIdentifier(tmp);
            tmp = scooter.getStatus();
            if (tmp != null && !tmp.isBlank()) existingScooter.setStatus(tmp);
            Integer i = scooter.getBatteryLevel();
            if (i != null ) existingScooter.setBatteryLevel(i);
            Zone zn = scooter.getZone();
            if (zn != null ) existingScooter.setZone(zn);
            i = scooter.getSpeedLimit();
            if (i != null ) existingScooter.setSpeedLimit(i);
            return scooterRepository.save(existingScooter);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteScooter(Long id) {
        return scooterRepository.findById(id).map(scooter -> {
            scooterRepository.delete(scooter);
            return true;
        }).orElse(false);
    }
}