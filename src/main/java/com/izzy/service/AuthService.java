package com.izzy.service;

import com.izzy.exception.BadRequestException;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.payload.request.SignupRequest;
import com.izzy.repository.RoleRepository;
import com.izzy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user
     *
     * @param signupRequest provided data {@link SignupRequest}
     * @return saved user data {@link User}
     * @throws BadRequestException throws when some incorrectness in request
     */
    public User registerUser(SignupRequest signupRequest) throws BadRequestException {
        User user = new User();
        user.setFirstName(signupRequest.getFirstname());
        user.setPhoneNumber(signupRequest.getPhonenumber());
        String password = signupRequest.getPassword();
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
//        user.setPassword(signupRequest.getPassword());
        Set<String> strRoles = signupRequest.getRole();
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
     * User login processing
     *
     * @param phoneNumber provided unique user identifier (phone number)
     * @param rawPassword provided password
     * @return existing user or null otherwise
     */
    public User login(String phoneNumber, String rawPassword) {
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user;
            }
        }
        return null;
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

    /**
     * Returns User details by user unique identifier (phone number)
     *
     * @param phoneNumber user identifier
     * @return user details {@link UserPrincipal}
     */
    public UserPrincipal getUserByUserIdentifier(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(UserPrincipal::build).orElse(null);
    }
}