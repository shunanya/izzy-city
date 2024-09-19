package com.izzy.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ZoneRepositoryTest {

    @Autowired
    private ZoneRepository zoneRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Create and configure ObjectMapper
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Test
    void testZoneFindByNameWithoutZoneName() throws JsonProcessingException {
        Optional<Zone> zone = zoneRepository.findByName(null);
        System.out.println(zone);
        assertNotNull(zone);
        assertFalse(zone.isPresent());

        // Serialize users to JSON
        String zoneJson = objectMapper.writeValueAsString(zone);

        // Print the JSON string
        System.out.println(zoneJson);

        // Optionally, assert the JSON string is not null or empty
        assertNotNull(zoneJson);
        assertFalse(zoneJson.isEmpty());
    }
}