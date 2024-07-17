package com.izzy.service;

import com.izzy.model.User;
import com.izzy.model.Zone;
import com.izzy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
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