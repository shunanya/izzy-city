package com.izzy.service;

import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.Role;
import com.izzy.repository.RoleRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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

    /**
     * Converts list of roles name to List of roles id
     *
     * @param roles the list of existing roles name
     * @return the references list of Role {@link Role} id
     * @implNote The returned list may be smaller than the source list due to incorrect role names
     */
    public List<Long> convertToRef(@NonNull List<String> roles) {
        List<Long> roleRef = new ArrayList<>();
        roles.forEach(r -> {
            Optional<Role> role = roleRepository.findByName(r);
            role.ifPresent(value -> roleRef.add(value.getId()));
        });
        return roleRef;
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
     * <p>Example usage:</p>
     * <pre>{@code
     * List<String> roles = getRolesFromParam("<= Manager");
     * }</pre>
     * <p> returned list contains ["Manager", "Supervisor", "Charger", "Scout"] </p>
     * <pre>{@code
     *  List<String> roles = getRolesFromParam("Admin, Scout, Creator");
     * }</pre>
     * <p>returned list contains ["Admin", "scout"] <br>
     * 'Creator' role excluded due to not recognized</p>
     */
    public List<String> getRolesFromParam(@NonNull String roleParam){
        String tmp = roleParam.replaceAll("\\s", "");
        String[] sp = tmp.split("(<=|>=|<|>)");
        if (sp.length > 1) {
            sp[0] = tmp.replace(sp[1], "");
            if (!roles.containsKey(sp[1])) {
                throw new UnrecognizedPropertyException("role", sp[1]);
            } else {
                List<String> role = new ArrayList<>();
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
                    default -> throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", tmp));
                }
                return role;
            }
        } else {
            sp = tmp.split(",");
            for (String role: sp) {
                if (!roles.containsKey(role)) {
                    throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", role));
                }
            }
        }
        return Arrays.stream(sp).toList();
    }
}