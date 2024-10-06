package com.izzy.repository;

import com.izzy.model.Scooter;

import com.izzy.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ScooterRepositoryTest {

    @Autowired
    ScooterRepository scooterRepository;
    @Autowired
    ZoneRepository zoneRepository;

    private final String zoneName = "zone_test";

    @BeforeEach
    void setUp() {
        Zone zone = zoneRepository.findByName(zoneName).orElse(zoneRepository.save(new Zone(1L,zoneName)));

        scooterRepository.save(new Scooter(1L, "1", "Active", 110, 150, zone));
        scooterRepository.save(new Scooter(2L, "2", "Active", 120, 140, zone));
        scooterRepository.save(new Scooter(3L, "3", "Active", 130, 130, zone));
    }

    // Retrieve scooters with battery level within a valid range
    @Test
    public void test_retrieve_scooters_with_valid_battery_range() {

        List<Scooter> scooters = scooterRepository.findScootersByBatteryLevelBetween(100, 120);

        assertNotNull(scooters, "List of scooters shouldn't be null");
        assertFalse(scooters.isEmpty(), "List of scooters shouldn't be empty");
        assertEquals(2, scooters.size());
    }

    // Handle minimum battery level equal to maximum battery level
    @Test
    public void test_min_battery_level_equals_max_battery_level() {
        List<Scooter> scooters = scooterRepository.findScootersByBatteryLevelBetween(110, 110);

        assertNotNull(scooters, "List of scooters shouldn't be null");
        assertFalse(scooters.isEmpty(), "List of scooters shouldn't be empty");
        assertEquals(1, scooters.size(), "1 item should be found");
    }

    @Test
    public void test_retrieve_scooters_with_valid_zone_name() {
        List<Scooter> scooters = scooterRepository.findScootersByZone_Name(zoneName);

        assertNotNull(scooters, "List of scooters shouldn't be null");
        assertFalse(scooters.isEmpty(), "List of scooters shouldn't be empty");
        assertTrue(scooters.size() >= 1, "3 items should be found");
    }

    @Test
    public void test_retrieve_scooters_by_filter(){
        List<Scooter> scooters = scooterRepository.findByFilter(null, 110, 130, 110, 160, "Active", zoneName);

        assertNotNull(scooters, "List of scooters shouldn't be null");
        assertFalse(scooters.isEmpty(), "List of scooters shouldn't be empty");
        assertTrue(scooters.size() >= 1, "3 items should be found");
    }
}