package com.izzy.service;

import com.izzy.model.Zone;
import com.izzy.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Zone createZone(String zoneName) {
        return zoneRepository.save(new Zone(zoneName));
    }

    @Transactional
    public Zone updateZone(Long id, String zoneName) {
        return zoneRepository.findById(id).map(existingZone -> {
            existingZone.setName(zoneName);
            return zoneRepository.save(existingZone);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteZone(Long id) {
        return zoneRepository.findById(id).map(zone -> {
            zoneRepository.delete(zone);
            return true;
        }).orElse(false);
    }
}