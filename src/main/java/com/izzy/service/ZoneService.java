package com.izzy.service;

import com.izzy.model.Zone;
import com.izzy.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {
    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id).orElse(null);
    }

    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }

    public Zone updateZone(Long id, Zone zone) {
        return zoneRepository.findById(id).map(existingZone -> {
            existingZone.setName(zone.getName());
            return zoneRepository.save(existingZone);
        }).orElse(null);
    }

    public boolean deleteZone(Long id) {
        return zoneRepository.findById(id).map(zone -> {
            zoneRepository.delete(zone);
            return true;
        }).orElse(false);
    }
}