package com.izzy.service;

import com.izzy.exception.AccessDeniedException;
import com.izzy.exception.BadRequestException;
import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.UnrecognizedPropertyException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.UserInfo;
import com.izzy.repository.UserRepository;
import com.izzy.repository.ZoneRepository;
import com.izzy.security.custom.service.CustomService;
import com.izzy.security.utils.Utils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    public User getUserFromUserRequest(@Nullable Long id, @NonNull UserRequest userRequest) {
        boolean createUser = (id == null);
        User user = (id == null) ? new User() : userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        String tmp = userRequest.getFirstName();
        if (tmp != null && !tmp.isBlank()) user.setFirstName(tmp);
        tmp = userRequest.getLastName();
        if (tmp != null && !tmp.isBlank()) user.setLastName(tmp);
        tmp = userRequest.getPhoneNumber();
        if (tmp != null && !tmp.isBlank()) {
            if (Utils.isCorrectPhoneNumber(tmp)) user.setPhoneNumber(tmp);
            else throw new BadRequestException("Invalid phone number");
        }
        tmp = userRequest.getPassword();
        if (tmp != null && !tmp.isBlank()) user.setPassword(passwordEncoder.encode(tmp));
        else if (createUser) {// Create temporary password (last 6 digits of phone number)
            tmp = user.getPhoneNumber();
            user.setPassword(passwordEncoder.encode(tmp.substring(tmp.length() - 6)));
        }
        tmp = userRequest.getGender();
        if (tmp != null && !tmp.isBlank()) user.setGender(tmp);
        LocalDate ld = userRequest.getDateOfBirth();
        if (ld != null && !ld.toString().isBlank()) user.setDateOfBirth(ld);
        tmp = userRequest.getZone();
        if (tmp != null && !tmp.isBlank()) {
            Optional<Zone> existingZone = zoneRepository.findByName(tmp);
            if (existingZone.isPresent()) user.setZone(existingZone.get());
            else throw new IllegalArgumentException(String.format("Error: Provided zone named '%s' not found", tmp));
        }
        tmp = userRequest.getShift();
        if (tmp != null && !tmp.isEmpty()) user.setShift(tmp);
        Long aLong = userRequest.getCreatedBy();
        if (createUser) {
            user.setCreatedBy(customService.currentUserId());
        } else if (aLong != null) {
                throw new IllegalArgumentException("Error: not allow to change Creator in already existing user data.");
        }
        Timestamp ts = userRequest.getCreatedAt();
        if (createUser) {
            user.setCreatedAt(Timestamp.from(Instant.now()));
        } else if (ts != null) {
            throw new IllegalArgumentException("Error: not allow to change creation date in already existing user data.");
        }
        aLong = userRequest.getUserManager();
        if (aLong != null) {
            if (userRepository.findById(aLong).isPresent()) user.setUserManager(aLong);
            else throw new IllegalArgumentException(String.format("Error: userManager with ID '%s' not found.", aLong));
        } else if (createUser){
            throw new BadRequestException("Error: User Manager should be defined.");
        }
        List<String> rawRole = userRequest.getRole();
        if (rawRole != null && !rawRole.isEmpty()) {
            List<Role> roles = rawRole.stream().map(roleService::getRoleByName).filter(Objects::nonNull).collect(Collectors.toList());
            if (roles.isEmpty()) {
                throw new BadRequestException("Error: User roles are not being recognized.");
            }
            user.setRoles(roles);
        }
        return user;
    }

    /**
     * Converts User structure to UserInfo
     *
     * @param user user entity {@link User}
     * @return UserInfo structured data {@link UserInfo}
     */
    public UserInfo connvertUserToUserInfo(@NonNull User user, boolean shortView) {
        User headOfUser = (user.getUserManager() == null) ? null : userRepository.findById(user.getUserManager()).orElse(null);
        return new UserInfo(user, headOfUser, shortView);
    }

    /**
     * Returns filtered or all users list
     *
     * @param viewType    optional parameter to get 'simple', 'short' and 'detailed' user data view (default is 'simple')
     * @param firstName   optional filtering parameter
     * @param lastName    optional filtering parameter
     * @param phoneNumber optional filtering parameter
     * @param gender      optional filtering parameter
     * @param shift       optional filtering parameter
     * @param zone        optional filtering parameter
     * @param roles       optional filtering parameter (for detail see {@link  RoleService#getRolesFromParam getRolesFromParam} method definitions
     * @return list of users
     */
    public List<?> getUsers(
            String viewType,
            String firstName,
            String lastName,
            String phoneNumber,
            String gender,
            String shift,
            String zone,
            String roles) {
        List<User> users;
        Set<String> availableRoles = customService.getCurrenUserAvailableRoles();
        if (firstName == null && lastName == null && phoneNumber == null && gender == null && zone == null && shift == null && roles == null) {
            users = userRepository.findAll();
        } else {
            // Detect current user available roles and combine the specified role filters with the current user's available roles.
            if (roles != null && !roles.isBlank()) {
                availableRoles = roleService.combineRoles(roleService.getRolesFromParam(roles), availableRoles);
//                users = userRepository.findUsersByFilters(firstName, lastName, phoneNumber, gender, zone, shift, availableRoles);
            }
            users = userRepository.findUsersByFilters(firstName, lastName, phoneNumber, gender, shift);
        }
        List<User> usersList = new ArrayList<>();
        for (User u : users) {
            if (availableRoles.containsAll(u.getRolesName()) && (zone == null || (u.getZone() != null && zone.equalsIgnoreCase(u.getZone().getName())))) {
                usersList.add(u);
            }
        }
        switch (viewType) {
            case "simple" -> {
                return usersList;
            }
            case "short" -> {
                return usersList.stream().map(user -> connvertUserToUserInfo(user, true)).collect(Collectors.toList());
            }
            case "detailed" -> {
                return usersList.stream().map(user -> connvertUserToUserInfo(user, false)).collect(Collectors.toList());
            }
            default -> throw new UnrecognizedPropertyException(String.format("unrecognized parameter '%s'", viewType));
        }
    }

    /**
     * Returns user data by user id
     *
     * @param id user id
     * @return user {@link User} on success
     * @throws AccessDeniedException     if operation is not permitted for current user
     * @throws ResourceNotFoundException if the user with defined id is not found.
     */
    public User getUserById(@NonNull Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
        if (customService.checkAllowability(user, true)) {
            return user;
        } else
            throw new AccessDeniedException("not allowed to request user with above your role");
    }

    /**
     * Saves user data into storage
     *
     * @param user given user data
     * @return saved user data on success
     */
    @Transactional
    public User saveUser(@NonNull User user) {
        if (customService.checkAllowability(user))
            return userRepository.save(user);
        else
            throw new AccessDeniedException("not allowed to create user with above your role");
    }

    /**
     * Updates existing user data
     *
     * @param id   user id
     * @param user user data
     * @return saved user data on success
     */
    @Transactional
    public User updateUser(@NonNull Long id, @NonNull User user) {
        if (customService.checkAllowability(user, true)) {
            if (user.getId().equals(id)) {
                return userRepository.save(user);
            } else
                throw new IllegalArgumentException("User ID mismatch between the updating and the requesting.");
        } else
            throw new AccessDeniedException("not allowed to update user with above your role");
    }

    public boolean existsUser(@NonNull Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Removes user data from storage
     *
     * @param id user id to be deleted
     * @throws AccessDeniedException     if operation is not permitted for current user
     * @throws ResourceNotFoundException if the user with defined id is not found.
     */
    @Transactional
    public void deleteUser(@NonNull Long id) {
        if (customService.checkAllowability(getUserById(id))) {
            userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            userRepository.deleteById(id);
        } else
            throw new AccessDeniedException("not allowed to delete user with above your role");
    }

}