package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.payload.request.UserRequest;
import com.izzy.repository.RoleRepository;
import com.izzy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user
     *
     * @param UserRequest provided data {@link UserRequest}
     * @return saved user data {@link User}
     * @throws BadRequestException throws when some incorrectness in request
     */
    public User registerUser(UserRequest UserRequest) throws BadRequestException {
        User user = new User();
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
