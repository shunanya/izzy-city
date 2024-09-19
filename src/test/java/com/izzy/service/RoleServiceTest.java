package com.izzy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.Role;
import com.izzy.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class RoleServiceTest {

    private final Map<String, Integer> roles = new HashMap<>();

    private RoleService roleService;
    private RoleRepository roleRepository;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Initializing Default Roles
        roles.put("Admin", 5);
        roles.put("Manager", 4);
        roles.put("Supervisor", 3);
        roles.put("Charger", 2);
        roles.put("Scout", 1);

        // Create and configure ObjectMapper
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        roleRepository = mock(RoleRepository.class);
        roleService = new RoleService(roleRepository);
    }

    @Test
    void getRolesFromParam_WithCorrectCondition() {
        String param = "<Manager";

        List<String> roles = roleService.getRolesFromParam(param);
        assert !roles.isEmpty() : "Error: result is empty";
        System.out.printf("'%s' => %s", param, roles);
    }

    @Test
    void getRolesFromParam_WithInCorrectCondition() {
        String param = "=Manager";
        UnrecognizedPropertyException ex = assertThrows(UnrecognizedPropertyException.class, () -> roleService.getRolesFromParam(param));
        assertEquals("unrecognized parameter '=Manager'", ex.getMessage());
    }

    @Test
    void getRolesFromParam_WithCorrectEnumeration() {
        String param = "Manager, Scout";

        List<String> roles = roleService.getRolesFromParam(param);
        assert !roles.isEmpty() : "Error: result is empty";
        assert roles.size() == 2 : "List of roles size should be 2";
        System.out.printf("'%s' => %s", param, roles);
    }

    @Test
    void getRolesFromParam_WithPartlyCorrectEnumeration() {
        String param = "Manager, Scout, Creator";

        UnrecognizedPropertyException ex = assertThrows(UnrecognizedPropertyException.class, () -> roleService.getRolesFromParam(param));
        assertEquals("unrecognized parameter 'Creator'", ex.getMessage());
    }

    @Test
    void convertToRef_WithExistingRoles() {
        List<String> roleNames = new ArrayList<>() {{
            add("Admin");
            add("Scout");
        }};
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(new Role("Admin")));
        when(roleRepository.findByName("Scout")).thenReturn(Optional.of(new Role("Scout")));

        List<Long> roleRefs = roleService.convertToRef(roleNames);

        if (roleRefs.isEmpty()) throw new AssertionError();
        assert roleRefs.size() == roleNames.size();

        System.out.println(roleNames + " => " + roleRefs);
    }

    @Test
    void convertToRef_WithoutExistingRoles() {
        List<String> roleNames = new ArrayList<>() {{
            add("Creator");
        }};
        when(roleRepository.findByName("Creator")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->roleService.convertToRef(roleNames));
     }

    @Test
    void convertToRef_WithPartlyFilledByExistingRoles() {
        List<String> roleNames = new ArrayList<>() {{
            add("Admin");
            add("Creator");
        }};
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(new Role("Admin")));
        when(roleRepository.findByName("Creator")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->roleService.convertToRef(roleNames));
     }

    @Test
    void convertToRoles_WithCorrectCondition() throws JsonProcessingException {
        String param = "<Manager";

        List<String> roles = roleService.getRolesFromParam(param);
        assert !roles.isEmpty() : "Error: result list is empty";

        Role role = new Role("", new ArrayList<>(Arrays.asList(1L, 2L)));
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));

        Set<Role> roleSet = roleService.convertToRoles(roles);
        assert !roleSet.isEmpty() : "Error: result Set is empty";

        System.out.printf("'%s' => %s", param, objectMapper.writeValueAsString(roleSet));
    }

    @Test
    void combineRoles_WithNonBlankCollections() {
        List<String> requiredList = new ArrayList<>() {{
            add("Manager");
            add("Supervisor");
            add("Charger");
            add("Scout");
        }};
        List<String> currentList = new ArrayList<>() {{
            add("Supervisor");
            add("Charger");
            add("Scout");
        }};

        List<String> original = new ArrayList<>(requiredList);
        List<String> resultList = roleService.combineRoles(requiredList, currentList);
        assert resultList != null && !resultList.isEmpty() : "Error: result List is empty";
        assert resultList.size() == 3 : "Error: result list size should be 3";

        System.out.printf("'%s' combine with '%s' => '%s'", original, currentList, resultList);
    }

    @Test
    void combineRoles_WithSupperCollections() {
        List<String> requiredList = new ArrayList<>() {{
            add("Admin");
            add("Manager");
            add("Supervisor");
            add("Charger");
            add("Scout");
        }};
        List<String> currentList = new ArrayList<>() {{
            add("Supervisor");
            add("Charger");
            add("Scout");
        }};

        List<String> original = new ArrayList<>(requiredList);
        List<String> resultList = roleService.combineRoles(requiredList, currentList);
        assert resultList != null && !resultList.isEmpty() : "Error: result List is empty";
        assert resultList.size() == 3 : "Error: result list size should be 3";

        System.out.printf("'%s' combine with '%s' => '%s'", original, currentList, resultList);
    }

    @Test
    void convertToRoles_WithPartlyUserAttached() throws JsonProcessingException {
        List<String> list = new ArrayList<>(){{add("Admin"); add("Manager"); add("Supervisor");}};

        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(new Role("", new ArrayList<>(Arrays.asList(1L, 2L)))));

        Set<Role> roles = roleService.convertToRoles(list);

        assert roles.size() > 0 : "Error: blank set is received";

        String rolesStringify = objectMapper.writeValueAsString(roles);
        System.out.printf("%s => %s", list, rolesStringify);
    }
}