package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.payload.request.UserRequest;
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

    public User getUserFromUserRequest (@NonNull UserRequest userRequest, Boolean createUser){
        User user = new User();
        String tmp = userRequest.getFirstName();
        if (tmp != null && !tmp.isBlank()) user.setFirstName(tmp);
        tmp = userRequest.getLastName();
        if (tmp != null && !tmp.isBlank()) user.setLastName(tmp);
        tmp = userRequest.getPhoneNumber();
        if (tmp != null && !tmp.isBlank()) user.setPhoneNumber(tmp);
        tmp = userRequest.getPassword();
        if (tmp!= null && !tmp.isBlank()) user.setPassword(tmp);
        tmp = userRequest.getGender();
        if (tmp != null && !tmp.isBlank()) user.setGender(tmp);
        LocalDate ld = userRequest.getDate_of_birth();
        if (ld != null && !ld.toString().isBlank()) user.setDateOfBirth(ld);
        tmp = userRequest.getZone();
        if (tmp != null && !tmp.isBlank()) {
            Optional<Zone> existingZone = zoneRepository.findByName(tmp);
            existingZone.ifPresent(user::setZone);
        }
        tmp = userRequest.getShift();
        if (tmp != null && !tmp.isEmpty()) user.setShift(tmp);
        Long id = userRequest.getCreated_by();
        if (id != null) {
            Optional<User> existingUser = userRepository.findById(id);
            existingUser.ifPresent(user::setCreatedBy);
        }
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

    public List<User> getUsers(String firstName, String lastName, String phoneNumber, String gender, String shift) {
        if (firstName != null || lastName != null || phoneNumber != null || gender != null || shift != null) {
            return userRepository.findUsersByFilters(firstName, lastName, phoneNumber, gender, shift);
        } else {
            return userRepository.findAll();
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(User user) {
        String password = user.getPassword();
        if (password != null && !password.isBlank()) {
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

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
            Zone zn = user.getZone();
            if (zn != null) existingUser.setZone(zn);
            tmp = user.getPassword();
            if (tmp != null && !tmp.isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(tmp));
            }
            return userRepository.save(existingUser);
        }).orElse(null);
    }

    public boolean deleteUser(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}