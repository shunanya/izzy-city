package com.izzy.service;

import com.izzy.model.UserEntity;
import com.izzy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserEntity> getUsers(String firstName, String lastName, String phoneNumber, String gender, String shift) {
        if (firstName != null || lastName != null || phoneNumber != null || gender != null || shift != null) {
            return userRepository.findUsersByFilters(firstName, lastName, phoneNumber, gender, shift);
        } else {
            return userRepository.findAll();
        }
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity createUser(UserEntity user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public UserEntity updateUser(Long id, UserEntity user) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setGender(user.getGender());
            existingUser.setDateOfBirth(user.getDateOfBirth());
            existingUser.setZone(user.getZone());
            existingUser.setShift(user.getShift());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
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