package com.izzy.security.custom.service;

import com.izzy.model.User;
import com.izzy.service.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomService {
    private static final Map<String, Integer> roles = new HashMap<>();

    static {
        // Default Roles
        roles.put("ROLE_Admin", 5);
        roles.put("ROLE_Manager", 4);
        roles.put("ROLE_Supervisor", 3);
        roles.put("ROLE_Charger", 2);
        roles.put("ROLE_Scout", 1);
    }

    public boolean checkAllowability(@NonNull User requestedUser) {
        // Detects requesting role
        UserPrincipal requestingUserDetails = UserPrincipal.build(requestedUser);
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
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
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
        return currentRole >= requestingRole;
    }

}
