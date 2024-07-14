package com.izzy.service;

import com.izzy.model.ZoneEntity;
import com.izzy.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {
    @Autowired
    private ZoneRepository zoneRepository;

    public List<ZoneEntity> getAllZones() {
        return zoneRepository.findAll();
    }

    public ZoneEntity getZoneById(Long id) {
        return zoneRepository.findById(id).orElse(null);
    }

    public ZoneEntity createZone(ZoneEntity zone) {
        return zoneRepository.save(zone);
    }

    public ZoneEntity updateZone(Long id, ZoneEntity zone) {
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