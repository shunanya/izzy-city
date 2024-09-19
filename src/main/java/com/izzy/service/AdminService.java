package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.payload.request.UserRequest;
import com.izzy.repository.RoleRepository;
import com.izzy.repository.UserRepository;
import com.izzy.repository.ZoneRepository;
import com.izzy.security.custom.service.CustomService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomService customService;

    public AdminService(UserRepository userRepository,
                        ZoneRepository zoneRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        CustomService customService) {
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.customService = customService;
    }

    /**
     * Registers a new user
     *
     * @param userRequest provided data {@link UserRequest}
     * @return saved user data {@link User}
     * @throws BadRequestException throws when some incorrectness in request
     */
    public User registerUser(UserRequest userRequest) throws BadRequestException {
        User user = new User();
        String tmp = userRequest.getFirstName();
        if (tmp != null && !tmp.isBlank()) user.setFirstName(tmp);
        tmp = userRequest.getLastName();
        if (tmp != null && !tmp.isBlank()) user.setLastName(tmp);
        tmp = userRequest.getPhoneNumber();
        if (tmp != null && !tmp.isBlank()) user.setPhoneNumber(tmp);
        else throw new BadRequestException("phone number must be defined.");
        tmp = userRequest.getPassword();
        if (tmp == null || tmp.isBlank()) tmp = user.getPhoneNumber().substring(2);
        user.setPassword(passwordEncoder.encode(tmp));
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
        user.setCreatedBy(customService.currentUserId());
        user.setCreatedAt(Timestamp.from(Instant.now()));
        List<String> rawRole = userRequest.getRole();
        if (rawRole != null && !rawRole.isEmpty()) {
            List<Role> roles = new ArrayList<>();
            rawRole.forEach(r -> roleRepository.findByName(r).ifPresent(roles::add));
            user.setRoles(roles);
            if (roles.isEmpty()) {
                throw new BadRequestException("Error: User roles are not being recognized.");
            }
        }
        List<String> listRoles = List.of("Scout", "Charger");
        boolean isScoutOrCharger = false;
        for (Role role : user.getRoles()) {
            isScoutOrCharger = isScoutOrCharger || listRoles.contains(role.getName());
        }
        Long aLong = userRequest.getUserManager();
        if (aLong != null) {
            if (isScoutOrCharger) {
                if (userRepository.findById(aLong).isPresent()) user.setUserManager(aLong);
                else
                    throw new IllegalArgumentException(String.format("Error: Head-user with ID '%s' not found.", aLong));
            } else {
                throw new BadRequestException("Error: Only Scout or Charger roles can have a user manager.");
            }
        } else if (isScoutOrCharger) {
            throw new BadRequestException("Error: The user manager must be assigned for Scout or Charger roles.");
        }
        return userRepository.save(user);
    }

    /**
     * Checks for user data exist in storage
     *
     * @param phoneNumber unique user identifier (phone number)
     * @return True on exists
     */
    public Boolean existByUserIdentifier(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }


}
