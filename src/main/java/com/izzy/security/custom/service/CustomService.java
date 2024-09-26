package com.izzy.security.custom.service;

import com.izzy.exception.CustomException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.Order;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.repository.OrderRepository;
import com.izzy.repository.RoleRepository;
import com.izzy.repository.UserRepository;
import com.izzy.service.RoleService;
import com.izzy.service.user_details.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomService {
    private static final Map<String, Integer> roles = new HashMap<>();

    static {
        // Initializing Default Roles
        roles.put("ROLE_Admin", 5);
        roles.put("ROLE_Manager", 4);
        roles.put("ROLE_Supervisor", 3);
        roles.put("ROLE_Charger", 1);
        roles.put("ROLE_Scout", 1);
    }

    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CustomService(RoleRepository roleRepository, RoleService roleService, UserRepository userRepository, OrderRepository orderRepository) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public boolean checkAllowability(@NonNull Long userId, boolean canActOnHimself) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return checkAllowability(user, canActOnHimself);
    }

    public boolean checkAllowability(@NonNull User requestedUser) {
        return checkAllowability(requestedUser, false);
    }

    public boolean checkAllowability(@NonNull User requestedUser, boolean canActOnHimself) {
        // Detects requesting role
        UserPrincipal userPrincipal = UserPrincipal.build(requestedUser);
        Long requestedUserId = userPrincipal.getId();
        Set<String> auths = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
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
            auths = currentUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
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

    public boolean checkAllowability(@NonNull Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return checkAllowability(order);
    }

    public boolean checkAllowability(@NonNull Order order) {
        Long createdUserId = order.getCreatedBy();
        User user = userRepository.findById(createdUserId).orElseThrow(() -> new CustomException(500, "Error: Order with erroneous 'createdBy' field: " + createdUserId));
        return checkAllowability(user);
    }

    /**
     * Retrieve all roles for currently signed-in user
     *
     * @return the list of roles
     */
    public Set<Role> getCurrenUserRoles() {
        Set<String> auths = new HashSet<>();
        // Obtains current user role
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            auths = currentUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        }
        Set<Role> roleSet = new HashSet<>();//new ArrayList<>();
        for (String auth : auths) {
            if (auth != null) roleRepository.findByName(auth.replace("ROLE_", "")).ifPresent(roleSet::add);
        }
        return roleSet;
    }

    /**
     * Retrieve the highest role of the currently signed-in user.
     * <p>
     * User can have a few roles. The current method obtain the highest role.
     * </p>
     *
     * @return {@link Role}
     */
    public Role getCurrenUserMaxRole() {
        Set<String> auths = new HashSet<>();
        // Detects current user role
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth != null && currenUserAuth.isAuthenticated()) {
            // Get the UserDetails object
            UserPrincipal currentUserDetails = (UserPrincipal) currenUserAuth.getPrincipal();
            auths = currentUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
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

    /**
     * Retrieve the user roles that the signed-in user can manage.
     *
     * @return the list of roles name
     */
    public Set<String> getCurrenUserAvailableRoles() {
        // Detect current user available roles
        Role currentUserMaxRole = getCurrenUserMaxRole();
        return roleService.getRolesFromParam("<" + currentUserMaxRole.getName());
    }

    /**
     * Retrieve details for currently signed-in user
     *
     * @return {@link UserPrincipal}
     */
    public UserPrincipal currentUserDetails() {
        Authentication currenUserAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currenUserAuth == null || !currenUserAuth.isAuthenticated()) {
            throw new AuthorizationServiceException("User is not authorized");
        }
        // Get the UserDetails object
        return (UserPrincipal) currenUserAuth.getPrincipal();
    }

    /**
     * Retrieve the ID of the currently signed-in user.
     *
     * @return user id
     */
    public Long currentUserId() {
        return currentUserDetails().getId();
    }

    /**
     * Retrieve the manager ID of the currently signed-in user
     *
     * @return user's manager id
     */
    public Long currentUserHeadId() {
        return currentUserDetails().getHeadForUserId();
    }
}
