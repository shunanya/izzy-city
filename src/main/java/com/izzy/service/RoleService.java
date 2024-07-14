package com.izzy.service;

import com.izzy.model.Role;
import com.izzy.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role role) {
        return roleRepository.findById(id).map(existingRole -> {
            existingRole.setName(role.getName());
            return roleRepository.save(existingRole);
        }).orElse(null);
    }

    public boolean deleteRole(Long id) {
        return roleRepository.findById(id).map(role -> {
            roleRepository.delete(role);
            return true;
        }).orElse(false);
    }
}