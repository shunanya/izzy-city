package com.izzy.service;

import com.izzy.model.RoleEntity;
import com.izzy.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    public RoleEntity getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public RoleEntity createRole(RoleEntity role) {
        return roleRepository.save(role);
    }

    public RoleEntity updateRole(Long id, RoleEntity role) {
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