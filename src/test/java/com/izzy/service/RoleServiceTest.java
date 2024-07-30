package com.izzy.service;

import com.izzy.exception.UnrecognizedPropertyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    private final Map<String, Integer> roles = new HashMap<>();

    @BeforeEach
    void setUp() {
        // Initializing Default Roles
        roles.put("Admin", 5);
        roles.put("Manager", 4);
        roles.put("Supervisor", 3);
        roles.put("Charger", 2);
        roles.put("Scout", 1);
    }

    @Test
    void getRolesFromParam_WithCorrectCondittion(){
        String param = "<Manager";
        
        List<String> roles = roleService.getRolesFromParam(param);
        assert !roles.isEmpty() : "Error: result is empty";
        System.out.printf("'%s' => %s", param, roles);
    }

    @Test
    void getRolesFromParam_WithInCorrectCondittion(){
        String param = "=Manager";
        UnrecognizedPropertyException ex = assertThrows(UnrecognizedPropertyException.class, () -> roleService.getRolesFromParam(param));
        assertEquals("unrecognized parameter '=Manager'", ex.getMessage());
    }

    @Test
    void getRolesFromParam_WithCorrectEnumeration(){
        String param = "Manager, Scout";

        List<String> roles = roleService.getRolesFromParam(param);
        assert !roles.isEmpty() : "Error: result is empty";
        assert roles.size() == 2: "List of roles size should be 2";
        System.out.printf("'%s' => %s", param, roles);
    }

    @Test
    void getRolesFromParam_WithPartlyCorrectEnumeration(){
        String param = "Manager, Scout, Creator";

        UnrecognizedPropertyException ex = assertThrows(UnrecognizedPropertyException.class, () -> roleService.getRolesFromParam(param));
        assertEquals("unrecognized parameter 'Creator'", ex.getMessage());
    }

    @Test
    void convertToRef_WithExistingRoles() {
        List<String> roleNames = new ArrayList<>(){{add("Admin"); add("Scout");}};

        List<Long> roleRefs = roleService.convertToRef(roleNames);
        if (roleRefs.isEmpty()) throw new AssertionError();
        assert roleRefs.size() == roleNames.size();

        System.out.println(roleNames+" => "+roleRefs);
    }

    @Test
    void convertToRef_WithoutExistingRoles() {
        List<String> roleNames = new ArrayList<>(){{add("Creator");}};

        List<Long> roleRefs = roleService.convertToRef(roleNames);
        assert roleRefs.isEmpty() : "Not Empty list is received";

        System.out.println(roleNames+" => "+roleRefs);
    }

    @Test
    void convertToRef_WithPartlyFilledByExistingRoles() {
        List<String> roleNames = new ArrayList<>(){{add("Admin"); add("Creator");}};

        List<Long> roleRefs = roleService.convertToRef(roleNames);
        assert !roleRefs.isEmpty() : "Empty list is received";
        assert roleRefs.size() < roleNames.size();

        System.out.println(roleNames+" => "+roleRefs);
    }
}