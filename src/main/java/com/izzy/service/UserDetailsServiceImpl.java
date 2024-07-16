package com.izzy.service;

import com.izzy.model.User;
import com.izzy.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(@NotBlank String username) throws UsernameNotFoundException {
        return null;
    }

    @Transactional
    public UserDetails loadUserByUserIdentifier(@NotBlank String userIdentifier) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(userIdentifier).orElseThrow(() -> new UsernameNotFoundException("Error: User not found"));
        return UserPrincipal.build(user);
    }
}