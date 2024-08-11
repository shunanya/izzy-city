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

/**
 * Service class for managing scooters.
 */
@Service
public class ScooterService {
    private final ScooterRepository scooterRepository;
    private final ZoneRepository zoneRepository;

    public ScooterService(ScooterRepository scooterRepository, ZoneRepository zoneRepository) {
        this.scooterRepository = scooterRepository;
        this.zoneRepository = zoneRepository;
    }

    /**
     * Retrieves all scooters from the repository.
     * 
     * @return a list of all scooters.
     */
    public List<Scooter> getAllScooters() {
        return scooterRepository.findAll();
    }

    /**
     * Retrieves a scooter by its ID.
     * 
     * @param id the ID of the scooter.
     * @return the scooter if found, otherwise null.
     */
    public Scooter getScooterById(Long id) {
        return scooterRepository.findById(id).orElse(null);
    }

    /**
     * Converts a ScooterRequest into a Scooter entity.
     * 
     * @param scooterRequest the request containing scooter data.
     * @param scooterId the ID of the scooter to update, or null for creation.
     * @return the constructed Scooter entity.
     */
    public Scooter getScooterFromScooterRequest(@NonNull ScooterRequest scooterRequest, @Nullable Long scooterId) {
        boolean creation = (scooterId == null);

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

    /**
     * Creates a new scooter in the repository.
     * 
     * @param scooter the scooter to create.
     * @return the created scooter.
     */
    @Transactional
    public Scooter createScooter(Scooter scooter) {
        return scooterRepository.save(scooter);
    }

    /**
     * Updates an existing scooter in the repository.
     * 
     * @param id the ID of the scooter to update.
     * @param scooter the new scooter data.
     * @return the updated scooter if found, otherwise null.
     */
    @Transactional
    public Scooter updateScooter(@NonNull Long id, @NonNull Scooter scooter) {
        return scooterRepository.findById(id).map(existingScooter -> {
            String tmp = scooter.getIdentifier();
            if (tmp != null && !tmp.isBlank()) existingScooter.setIdentifier(tmp);
            tmp = scooter.getStatus();
            if (tmp != null && !tmp.isBlank()) existingScooter.setStatus(tmp);
            Integer i = scooter.getBatteryLevel();
            if (i != null) existingScooter.setBatteryLevel(i);
            Zone zn = scooter.getZone();
            if (zn != null) existingScooter.setZone(zn);
            i = scooter.getSpeedLimit();
            if (i != null) existingScooter.setSpeedLimit(i);
            return scooterRepository.save(existingScooter);
        }).orElse(null);
    }

    /**
     * Deletes a scooter by its ID.
     * 
     * @param id the ID of the scooter to delete.
     * @return true if the scooter was deleted, false if not found.
     */
    @Transactional
    public boolean deleteScooter(Long id) {
        return scooterRepository.findById(id).map(scooter -> {
            scooterRepository.delete(scooter);
            return true;
        }).orElse(false);
    }
}
