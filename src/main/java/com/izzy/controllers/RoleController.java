package com.izzy.controllers;

import com.izzy.model.Role;
import com.izzy.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role != null) {
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Role> createRole(@RequestBody String roleName) {
        Role createdRole = roleService.createRole(roleName);
        return ResponseEntity.ok(createdRole);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody String roleName) {
        Role updatedRole = roleService.updateRole(id, roleName);
        if (updatedRole != null) {
            return ResponseEntity.ok(updatedRole);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (roleService.deleteRole(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}