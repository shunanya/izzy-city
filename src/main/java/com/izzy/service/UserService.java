package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.payload.request.UserRequest;
import com.izzy.payload.response.UserShortInfo;
import com.izzy.repository.RoleRepository;
import com.izzy.repository.UserRepository;
import com.izzy.repository.ZoneRepository;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ZoneRepository zoneRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Converts UserRequest class data into User entity data
     *
     * @param userRequest class that has to be converted
     * @param createUser  When TRUE (i.e., called from createUser), the current date is inserted into the created_at field.
     * @return User class on success
     */
    public User getUserFromUserRequest(@NonNull UserRequest userRequest, Boolean createUser) {
        User user = new User();
        String tmp = userRequest.getFirstName();
        if (tmp != null && !tmp.isBlank()) user.setFirstName(tmp);
        tmp = userRequest.getLastName();
        if (tmp != null && !tmp.isBlank()) user.setLastName(tmp);
        tmp = userRequest.getPhoneNumber();
        if (tmp != null && !tmp.isBlank()) user.setPhoneNumber(tmp);
        tmp = userRequest.getPassword();
        if (tmp != null && !tmp.isBlank()) user.setPassword(tmp);
        tmp = userRequest.getGender();
        if (tmp != null && !tmp.isBlank()) user.setGender(tmp);
        LocalDate ld = userRequest.getDate_of_birth();
        if (ld != null && !ld.toString().isBlank()) user.setDateOfBirth(ld);
        tmp = userRequest.getZone();
        if (tmp != null && !tmp.isBlank()) {
            Optional<Zone> existingZone = zoneRepository.findByName(tmp);
            if (existingZone.isPresent()) user.setZone(existingZone.get().getId());
            else throw new IllegalArgumentException(String.format("Error: Provided zone named '%s' not found", tmp));
        }
        tmp = userRequest.getShift();
        if (tmp != null && !tmp.isEmpty()) user.setShift(tmp);
        Long id = userRequest.getCreated_by();
        user.setCreatedBy((id != null && userRepository.findById(id).isPresent()) ? id : null);
//        if (id != null) {
//            Optional<User> existingUser = userRepository.findById(id);
//            existingUser.ifPresent(user -> user.setCreatedBy(id));
//        }
        Timestamp ts = userRequest.getCreated_at();
        if (createUser) {
            user.setCreatedAt(Timestamp.from(Instant.now()));
        } else if (ts != null) {
            user.setCreatedAt(ts);
        }
        id = userRequest.getHead_for_user();
        if (id != null) {
            Optional<User> existingUser = userRepository.findById(id);
            existingUser.ifPresent(user::setHeadForUser);
        }
        Set<String> rawRole = userRequest.getRole();
        if (rawRole != null && !rawRole.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            rawRole.forEach(r -> {
                Optional<Role> existingRole = roleRepository.findByName(r);
                existingRole.ifPresent(roles::add);
            });
            user.setRoles(roles);
            if (roles.isEmpty()) {
                throw new BadRequestException("Error: User roles are not being recognized.");
            }
        }
        return user;
    }

    public UserShortInfo convertUserToShort(User user) {
        String zoneName = null;
        Long ld = user.getZone();
        if (ld != null) {
            Optional<Zone> optZone = zoneRepository.findById(ld);
            if (optZone.isPresent()) zoneName = optZone.get().getName();
        }
        return new UserShortInfo(user.getId(), user.getFirstName(), user.getPhoneNumber(), user.getRoles(), zoneName);
    }

    /**
     * Returns filtered or all users list
     *
     * @param firstName   filtering parameter
     * @param lastName    filtering parameter
     * @param phoneNumber filtering parameter
     * @param gender      filtering parameter
     * @param zoneName        filtering parameter
     * @param shift       filtering parameter
     * @return list of users
     */
    public List<User> getUsers(String firstName, String lastName, String phoneNumber, String gender, String zoneName, String shift) {
        if (firstName != null || lastName != null || phoneNumber != null || gender != null || zoneName != null || shift != null) {
            Long zoneId = null;
            if (zoneName != null) {
                Optional<Zone> optZone = zoneRepository.findByName(zoneName);
                 if (optZone.isPresent()) zoneId = optZone.get().getId();
                else
                    throw new IllegalArgumentException(String.format("Error: provided zone '%s' isn't found in storage.", zoneName));
            }
            return userRepository.findUsersByFilters(firstName, lastName, phoneNumber, gender, zoneId, shift);
        } else {
            return userRepository.findAll();
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
        String password = user.getPassword();
        if (password != null && !password.isBlank()) {
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    /**
     * Updates existing user data
     *
     * @param id   user id
     * @param user user data
     * @return saved user data on success
     */
    public User updateUser(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            String tmp = user.getFirstName();
            if (tmp != null) existingUser.setFirstName(tmp);
            tmp = user.getLastName();
            if (tmp != null) existingUser.setLastName(tmp);
            tmp = user.getPhoneNumber();
            if (tmp != null) existingUser.setPhoneNumber(tmp);
            tmp = user.getGender();
            if (tmp != null) existingUser.setGender(tmp);
            tmp = user.getShift();
            if (tmp != null) existingUser.setShift(tmp);
            LocalDate ld = user.getDateOfBirth();
            if (ld != null) existingUser.setDateOfBirth(ld);
            Long ln = user.getZone();
            if (ln != null) existingUser.setZone(ln);
            tmp = user.getPassword();
            if (tmp != null && !tmp.isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(tmp));
            }
            Long rawUserId = user.getCreatedBy();
            userRepository.findById(rawUserId).ifPresent(u -> existingUser.setCreatedBy(rawUserId));

            return userRepository.save(existingUser);
        }).orElse(null);
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