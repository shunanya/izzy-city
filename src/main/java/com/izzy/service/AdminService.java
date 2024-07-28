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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
/*
        user.setFirstName(UserRequest.getFirstName());
        String phoneNumber = UserRequest.getPhoneNumber();
        user.setPhoneNumber(phoneNumber);
        String tmp = UserRequest.getPassword();
        if (tmp != null && !tmp.isBlank()) {
            user.setPassword(passwordEncoder.encode(tmp));
        } else { // set temporary password = 6 last digits of phone number
            user.setPassword(passwordEncoder.encode(phoneNumber.substring(phoneNumber.length()-6)));
        }
        Set<String> strRoles = UserRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles != null && !strRoles.isEmpty()) {
            strRoles.forEach(role -> {
                Optional<Role> existingRole = roleRepository.findByName(role);
                existingRole.ifPresent(roles::add);
            });
        }
        if (roles.isEmpty()) {
            throw new BadRequestException("Error: user role is not defined correctly.");
        }
        user.setRoles(roles);
*/


        String tmp = userRequest.getFirstName();
        if (tmp != null && !tmp.isBlank()) user.setFirstName(tmp);
        tmp = userRequest.getLastName();
        if (tmp != null && !tmp.isBlank()) user.setLastName(tmp);
        tmp = userRequest.getPhoneNumber();
        if (tmp != null && !tmp.isBlank()) user.setPhoneNumber(tmp);
        tmp = userRequest.getPassword();
        if (tmp != null && !tmp.isBlank()) user.setPassword(passwordEncoder.encode(tmp));
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
        user.setCreatedBy(customService.currentUserId());
        user.setCreatedAt(Timestamp.from(Instant.now()));
        Long aLong = userRequest.getHeadForUser();
        if (aLong != null) {
            if (userRepository.findById(aLong).isPresent()) user.setHeadForUser(aLong);
            else throw new IllegalArgumentException(String.format("Error: Head-user with ID '%s' not found.", aLong));
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
        return userRepository.save(user);
    }

    /**
     * Checks for user data exist in storage
     *
     * @param phoneNumber unified user identifier (phone number)
     * @return True on exists
     */
    public Boolean existByUserIdentifier(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }


}
