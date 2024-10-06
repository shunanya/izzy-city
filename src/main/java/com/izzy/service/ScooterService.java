package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Scooter;
import com.izzy.model.Zone;
import com.izzy.payload.request.ScooterRequest;
import com.izzy.repository.ScooterRepository;
import com.izzy.repository.ZoneRepository;
import com.izzy.security.utils.Utils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * Retrieves a scooter by ID.
     *
     * @param id the ID of the scooter.
     * @return the scooter
     */
    public Scooter getScooterById(Long id) {
        return scooterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", id));
    }

    /**
     * Retrieve Scooter by unique identifier
     *
     * @param identifier the Scooter unique identifier
     * @return the scooter
     */
    public Scooter getScooterByIdentifier(@NonNull String identifier) {
        return scooterRepository.findScooterByIdentifier(identifier).orElseThrow(() -> new ResourceNotFoundException("Scooter", "identifier", identifier));
    }

    /**
     * Retrieve Scooters with a battery level within a specified range
     *
     * @param minBatteryLevel The minimum battery level (inclusive) to filter the scooters.
     * @param maxBatteryLevel The maximum battery level (inclusive) to filter the scooters
     * @return a list of Scooters whose battery levels fall within the specified range.
     */
    public List<Scooter> getAllScootersByBatteryLevel(int minBatteryLevel, int maxBatteryLevel) {
        return scooterRepository.findScootersByBatteryLevelBetween(minBatteryLevel, maxBatteryLevel);
    }

    /**
     * Retrieve Scooters with filtering
     *
     * @param batteryLevel the optional string representing range of battery level
     * @param speedLimit   the optional string representing the range of speed limit
     * @param status       the optional string representing the status of scooter
     * @param zoneName     the optional string representing the zone name where scooter is located
     * @return the list of scooters
     */
    public List<Scooter> getScootersByFiltering(@Nullable String identifier, @Nullable String batteryLevel, @Nullable String speedLimit, @Nullable String status, @Nullable String zoneName) {
        List<Integer> blr = Utils.parseDataRangeToPairOfInteger(batteryLevel);
        List<Integer> slr = Utils.parseDataRangeToPairOfInteger(speedLimit);
        return scooterRepository.findByFilter(identifier, blr.get(0), blr.get(1), slr.get(0), slr.get(1), status, zoneName);
    }

    /**
     * Converts a ScooterRequest into a Scooter entity.
     *
     * @param scooterRequest the request containing scooter data.
     * @param scooterId      the ID of the scooter to update, or null for creation.
     * @return the constructed Scooter entity.
     */
    public Scooter getScooterFromScooterRequest(@NonNull ScooterRequest scooterRequest, @Nullable Long scooterId) {
        boolean creation = (scooterId == null); // the creation in case scooterId is not defined
        Scooter scooter;
        if (creation) scooter = new Scooter();
        else { // try to find Scooter to be updated
            scooter = scooterRepository.findById(scooterId).orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", scooterId));
        }

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
        tmp = scooterRequest.getZoneName();
        if (tmp != null && !tmp.isBlank()) {
            String zoneName = tmp;
            scooter.setZone(zoneRepository.findByName(tmp).orElseThrow(() -> new ResourceNotFoundException("Zone", "name", zoneName)));
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
     * @param id      the ID of the scooter to update.
     * @param scooter the new scooter entity.
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
        }).orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", id));
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
        }).orElseThrow(() -> new ResourceNotFoundException("Scooter", "id", id));
    }
}
