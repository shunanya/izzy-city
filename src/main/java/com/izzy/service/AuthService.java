package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.model.User;
import com.izzy.repository.UserRepository;
import com.izzy.service.user_details.UserPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "phoneNumber", phoneNumber);
        }
        User user = userOptional.get();
        return passwordEncoder.matches(rawPassword, user.getPassword()) ? user : null;
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