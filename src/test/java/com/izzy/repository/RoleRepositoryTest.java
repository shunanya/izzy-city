package com.izzy.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

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
    void findByExistingRoleName() throws JsonProcessingException {
        String role = "Manager";
        Optional<Role> r = roleRepository.findByName(role);

        // assert the Role object is not null
        assertNotNull(r);
        assert r.isPresent();

        String roleJson = objectMapper.writeValueAsString(r.get());
        // Print the Role object
        System.out.println(roleJson);
    }

    @Test
    void findByExistingRoleLowerCaseName() throws JsonProcessingException {
        String role = "manager";
        Optional<Role> r = roleRepository.findByName(role);

        // assert the Role object is not null
        assertNotNull(r);
        assert r.isPresent(): "Role is not exists";

        String roleJson = objectMapper.writeValueAsString(r.get());
        // Print the Role object
        System.out.println(roleJson);
    }

    @Test
    void findByNotExistingRoleName() {
        String role = "Creator";
        Optional<Role> r = roleRepository.findByName(role);

        // assert the Role object is not null
        assert r.isEmpty() : "Role is Not Empty";
    }

}