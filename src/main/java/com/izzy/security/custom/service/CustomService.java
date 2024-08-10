package com.izzy.security.custom.service;

import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.repository.RoleRepository;
import com.izzy.service.RoleService;
import com.izzy.service.user_details.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomService {
    private static final Map<String, Integer> roles = new HashMap<>();

    static {
        // Initializing Default Roles
        roles.put("ROLE_Admin", 5);
        roles.put("ROLE_Manager", 4);
        roles.put("ROLE_Supervisor", 3);
        roles.put("ROLE_Charger", 2);
        roles.put("ROLE_Scout", 1);
    }

    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public CustomService(RoleRepository roleRepository, RoleService roleService) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    public boolean checkAllowability(@NonNull User requestedUser) {
        return checkAllowability(requestedUser, false);
    }

    public boolean checkAllowability(@NonNull User requestedUser, boolean canActOnHimself) {
        // Detects requesting role
        UserPrincipal requestingUserDetails = UserPrincipal.build(requestedUser);
        Long requestedUserId = requestingUserDetails.getId();
        List<String> auths = requestingUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        int requestingRole = 1;
        for (String auth : auths) {
            if (auth != null) {
                Integer i = roles.get(auth);
                if (i != null && i > requestingRole) requestingRole = i;
            }
        }
        // Detects current user role
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = 0L;
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            currentUserId = currentUserDetails.getId();
            auths = currentUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        }
        int currentRole = 1;
        for (String auth : auths) {
            if (auth != null) {
                Integer i = roles.get(auth);
                if (i != null && i > currentRole) currentRole = i;
            }
        }
        // check allowability
        return currentRole == roles.size() // role with max permissions - Admin
                || (canActOnHimself && requestedUserId.equals(currentUserId)) // current user does some action on themselves
                || currentRole > requestingRole; // current user role higher than requesting user
    }

    public Set<Role> getCurrenUserRoles() {
        List<String> auths = new ArrayList<>();
        // Obtains current user role
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            auths = currentUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        }
        Set<Role> roleSet = new HashSet<>();
        for (String auth : auths) {
            if (auth != null) roleRepository.findByName(auth.replace("ROLE_", "")).ifPresent(roleSet::add);
        }
        return roleSet;
    }

    public Role getCurrenUserMaxRoles() {
        List<String> auths = new ArrayList<>();
        // Detects current user role
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            auths = currentUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        }
        String currentRoleName = "ROLE_Scout";
        int currentRoleWeight = roles.get(currentRoleName);
        for (String auth : auths) {
            if (auth != null) {
                Integer i = roles.get(auth);
                if (i != null && i > currentRoleWeight) {
                    currentRoleWeight = i;
                    currentRoleName = auth.replace("ROLE_", "");
                }
            }
        }
        return roleRepository.findByName(currentRoleName).orElse(null);
    }

    public List<String> getCurrenUserAvailableRoles() {
        // Detect current user available roles
        Role currentUserMaxRole = getCurrenUserMaxRoles();
        return roleService.getRolesFromParam("<" + currentUserMaxRole.getName());
    }

    public Long currentUserId() {
        Long id = null;
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            id = currentUserDetails.getId();
        }
        return id;
    }
}
