package com.izzy.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

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
    public void testFindUsersByFiltersWithZone() throws JsonProcessingException {
        List<User> users = userRepository.findUsersByFilters(null, null, null, null, "z01", "day shift");
        assertNotNull(users);
        assertFalse(users.isEmpty());

        // Serialize users to JSON
        String usersJson = objectMapper.writeValueAsString(users);

        // Print the JSON string
        System.out.println(usersJson);

        // Optionally, assert the JSON string is not null or empty
        assertNotNull(usersJson);
        assertFalse(usersJson.isEmpty());
    }

    @Test
    public void testFindUsersByFiltersWithoutZone() throws JsonProcessingException {
        List<User> users = userRepository.findUsersByFilters(null, null, null, null, null, "day shift");

        assertNotNull(users);
        assertFalse(users.isEmpty());

        // Serialize users to JSON
        String usersJson = objectMapper.writeValueAsString(users);

        // Print the JSON string
        System.out.println(usersJson);

        // Optionally, assert the JSON string is not null or empty
        assertNotNull(usersJson);
        assertFalse(usersJson.isEmpty());
    }
}