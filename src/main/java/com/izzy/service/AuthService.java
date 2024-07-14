package com.izzy.service;

import com.izzy.model.UserEntity;
import com.izzy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserEntity user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public UserEntity login(String phoneNumber, String rawPassword) {
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        }
        return null;
    }

    public Boolean existByUserIdentifier(String phoneNumber){
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber);
        return user != null;
    }
}