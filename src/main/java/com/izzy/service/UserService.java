package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.UserInfo;
import com.izzy.payload.response.UserShortInfo;
import com.izzy.repository.UserRepository;
import com.izzy.repository.ZoneRepository;
import com.izzy.security.custom.service.CustomService;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final CustomService customService;

    public UserService(UserRepository userRepository,
                       ZoneRepository zoneRepository,
                       PasswordEncoder passwordEncoder,
                       RoleService roleService,
                       CustomService customService) {
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.customService = customService;
    }

    /**
     * Converts UserRequest class data into User entity data
     *
     * @param userRequest class that has to be converted
     * @param id          nullable value means Create User (the current date is inserted into the created_at field).
     * @return User class on success
     */
    public User getUserFromUserRequest(Long id, @NonNull UserRequest userRequest) {
        boolean createUser = (id == null);
        User user = (id == null) ? new User() : userRepository.findById(id).get();
        String tmp = userRequest.getFirstName();
        if (tmp != null && !tmp.isBlank()) user.setFirstName(tmp);
        tmp = userRequest.getLastName();
        if (tmp != null && !tmp.isBlank()) user.setLastName(tmp);
        tmp = userRequest.getPhoneNumber();
        if (tmp != null && !tmp.isBlank()) user.setPhoneNumber(tmp);
        tmp = userRequest.getPassword();
        if (tmp != null && !tmp.isBlank()) user.setPassword(passwordEncoder.encode(tmp));
        else {// Create temporary password (last 5 digits of phone number)
            tmp = user.getPhoneNumber();
            user.setPassword(passwordEncoder.encode(tmp.substring(tmp.length()-6)));
        }
        tmp = userRequest.getGender();
        if (tmp != null && !tmp.isBlank()) user.setGender(tmp);
        LocalDate ld = userRequest.getDateOfBirth();
        if (ld != null && !ld.toString().isBlank()) user.setDateOfBirth(ld);
        tmp = userRequest.getZone();
        if (tmp != null && !tmp.isBlank()) {
            Optional<Zone> existingZone = zoneRepository.findByName(tmp);
            if (existingZone.isPresent()) user.setZone(tmp);
            else throw new IllegalArgumentException(String.format("Error: Provided zone named '%s' not found", tmp));
        }
        tmp = userRequest.getShift();
        if (tmp != null && !tmp.isEmpty()) user.setShift(tmp);
        Long aLong = userRequest.getCreatedBy();
        if (createUser) {
            user.setCreatedBy(customService.currentUserId());
        } else if (aLong != null) {
            if (userRepository.findById(aLong).isPresent()) user.setCreatedBy(aLong);
            else
                throw new IllegalArgumentException(String.format("Error: Creator-user with ID '%s' not found.", aLong));
        }
        Timestamp ts = userRequest.getCreatedAt();
        if (createUser) {
            user.setCreatedAt(Timestamp.from(Instant.now()));
        } else if (ts != null) {
            user.setCreatedAt(ts);
        }
        aLong = userRequest.getHeadForUser();
        if (aLong != null) {
            if (userRepository.findById(aLong).isPresent()) user.setHeadForUser(aLong);
            else throw new IllegalArgumentException(String.format("Error: Head-user with ID '%s' not found.", aLong));
        }
        Set<String> rawRole = userRequest.getRole();
        if (rawRole != null && !rawRole.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            rawRole.forEach(r -> {
                Role existingRole = roleService.getRoleByName(r);
                if (existingRole != null) roles.add(existingRole);
            });
            user.setRoles(roles);
            if (roles.isEmpty()) {
                throw new BadRequestException("Error: User roles are not being recognized.");
            }
        }
        return user;
    }

    /**
     * Converts User structure to shortInfo
     *
     * @param user whole user entity
     * @return UserShortInfo data
     */
    public UserShortInfo convertUserToShort(User user) {
        return new UserShortInfo(user);
    }

    public UserInfo connvertUserToUserInfo(User user) {
        return new UserInfo(user);
    }

    /**
     * Returns filtered or all users list
     *
     * @param firstName   filtering parameter
     * @param lastName    filtering parameter
     * @param phoneNumber filtering parameter
     * @param gender      filtering parameter
     * @param zone        filtering parameter
     * @param shift       filtering parameter
     * @param roles       filtering parameter (for detail see {@link  RoleService#getRolesFromParam getRolesFromParam} method definitions
     * @return list of users
     */
    public List<User> getUsers(String firstName,
                               String lastName,
                               String phoneNumber,
                               String gender,
                               String zone,
                               String shift,
                               String roles) {
        if (firstName == null && lastName == null && phoneNumber == null && gender == null && zone == null && shift == null && roles == null) {
            return userRepository.findAll();
        } else {
            // Detect current user available roles
            List<String> availableRoles = customService.getCurrenUserAvailableRoles();
            // Combine the specified role filters with the current user's available roles.
            if (roles != null && !roles.isBlank()){
                availableRoles = roleService.combineRoles(roleService.getRolesFromParam(roles), availableRoles);
            }
            return userRepository.findUsersByFilters(firstName, lastName, phoneNumber, gender, zone, shift, availableRoles);
        }
    }

    /**
     * Returns user data by user id
     *
     * @param id user id
     * @return user {@link User} on success
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Saves user data into storage
     *
     * @param user given user data
     * @return saved user data on success
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates existing user data
     *
     * @param id   user id
     * @param user user data
     * @return saved user data on success
     */
    public User updateUser(@NonNull Long id, @NonNull User user) {
        if (!user.getId().equals(id)) {
            throw new IllegalArgumentException("The parameters for the updateUser method are incorrect.");
        }
        return userRepository.save(user);
    }

    public boolean existsUser(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Removes user data from storage
     *
     * @param id user id
     * @return True on success
     */
    public boolean deleteUser(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }

}