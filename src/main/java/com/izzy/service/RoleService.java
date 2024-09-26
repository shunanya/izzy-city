package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.Role;
import com.izzy.repository.RoleRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleService {

    private static final Map<String, Integer> roles = new HashMap<>();

    static {
        // Initializing Default Roles
        roles.put("Admin", 5);
        roles.put("Manager", 4);
        roles.put("Supervisor", 3);
        roles.put("Charger", 2);
        roles.put("Scout", 1);
    }

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

    @Transactional
    public Role createRole(String roleName) {
        return roleRepository.save(new Role(roleName));
    }

    @Transactional
    public Role updateRole(Long id, String roleName) {
        return roleRepository.findById(id).map(existingRole -> {
            existingRole.setName(roleName);
            return roleRepository.save(existingRole);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteRole(Long id) {
        return roleRepository.findById(id).map(role -> {
            roleRepository.delete(role);
            return true;
        }).orElse(false);
    }

    /**
     * Converts list of roles name to List of roles id
     *
     * <p>Note: The returned list may be smaller than the source list due to incorrect role names</p>
     * @param roles the list of existing roles name
     * @return the references list of Role {@link Role} id
     */
    public Set<Long> convertToRef(@NonNull Set<String> roles) {
        Set<Long> roleRef = new HashSet<>();
        roles.stream().map(roleRepository::findByName).forEach(role -> role.ifPresentOrElse(value -> roleRef.add(value.getId()), () -> {
            throw new ResourceNotFoundException("Role", "name", role);
        }));
        return roleRef;
    }

    public Set<Role> convertToRoles(@NonNull Set<String> roles) {
        Set<Role> roleSet = new HashSet<>();
        roles.forEach(r -> roleRepository.findByName(r).ifPresent(role -> {
            if (!role.getUsers().isEmpty()) roleSet.add(role);
        }));
        return roleSet;
    }

    /**
     * Validates and Composes a list of roles according to the specified role parameter.
     *
     * @param roleParam the conditions that define the final roles list.<br>
     *                  Possible values:
     *                  <ul>
     *                  <li>&lt; {one of existing role}
     *                  <li>> {one of existing role}
     *                  <li>&lt;= {one of existing role}
     *                  <li>>= {one of existing role}
     *                  <li>enumeration of roles
     *                  </ul>
     * @return the final roles list
     * @throws UnrecognizedPropertyException if roleParam or some its components are invalid
     *
     *                                       <p>Example usage:</p>
     *                                       <pre>{@code
     *                                       List<String> roles = getRolesFromParam("<= Manager");
     *                                       }</pre>
     *                                       <p> returned list contains ["Manager", "Supervisor", "Charger", "Scout"] </p>
     *                                       <pre>{@code
     *                                        List<String> roles = getRolesFromParam("Admin, Scout, Creator");
     *                                       }</pre>
     *                                       <p>returned list contains ["Admin", "scout"] <br>
     *                                       'Creator' role excluded due to not recognized</p>
     */
    public Set<String> getRolesFromParam(@NonNull String roleParam) {
        String tmp = roleParam.replaceAll("\\s", "");
        String[] sp = tmp.split("(<=|>=|<|>)");
        if (sp.length > 1) {
            sp[0] = tmp.replace(sp[1], "");
            if (!roles.containsKey(sp[1])) {
                throw new UnrecognizedPropertyException("role", sp[1]);
            } else {
                HashSet<String> role = new HashSet<>();
                int lim = roles.get(sp[1]);
                switch (sp[0]) {
                    case "<" -> // Request: not higher than sp[1]
                            roles.forEach((r, i) -> {
                                if (i < lim) role.add(r);
                            });
                    case ">" -> // Request: higher than sp[1]
                            roles.forEach((r, i) -> {
                                if (i > lim) role.add(r);
                            });
                    case ">=" -> // Request: higher or equal  sp[1]
                            roles.forEach((r, i) -> {
                                if (i >= lim) role.add(r);
                            });
                    case "<=" -> // Request: not higher or equal sp[1]
                            roles.forEach((r, i) -> {
                                if (i <= lim) role.add(r);
                            });
                    default ->
                            throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", tmp));
                }
                return role;
            }
        } else {
            sp = tmp.split(",");
            for (String role : sp) {
                if (!roles.containsKey(role)) {
                    throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", role));
                }
            }
        }
        return new HashSet<>(List.of(sp));
    }

    Set<String> combineRoles(@NonNull Set<String> requiredList, @NonNull Set<String> currentList) {
        requiredList.retainAll(currentList);
        return requiredList;
    }
}