package com.izzy.controllers;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Role;
import com.izzy.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing role-related operations.
 * Provides endpoints for creating, updating, and retrieving role information.
 * Handles access control and exception management.
 */
@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Retrieve all role
     *
     * @return list of role
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * Retrieves a role by their ID.
     *
     * @param id the ID of the role to retrieve.
     * @return a ResponseEntity containing the role.
     * @throws ResourceNotFoundException if the role is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role != null) {
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Creates a new role.
     *
     * @param roleName the creating role name.
     * @return a ResponseEntity containing a created role details.
     * @throws AccessDeniedException if operation is not permitted for current user
     */
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Role> createRole(@RequestBody String roleName) {
        Role createdRole = roleService.createRole(roleName);
        return ResponseEntity.ok(createdRole);
    }

    /**
     * Updates an existing role.
     *
     * @param id                   the ID of the role to update.
     * @param roleName the updating role name.
     * @return ResponseEntity containing an updated role details.
     * @throws ResourceNotFoundException if the role is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody String roleName) {
        Role updatedRole = roleService.updateRole(id, roleName);
        if (updatedRole != null) {
            return ResponseEntity.ok(updatedRole);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a role by their ID.
     *
     * @param id the ID of the role to delete.
     * @return ResponseEntity containing a success message.
     * @throws ResourceNotFoundException if the role is not found.
     * @throws AccessDeniedException     if operation is not permitted for current user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (roleService.deleteRole(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}