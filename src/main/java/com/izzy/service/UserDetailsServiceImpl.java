package com.izzy.service;

import com.izzy.model.User;
import com.izzy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(userIdentifier);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
//        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
//        for (Role role : user.getRoles()) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
//        }
//        return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), user.getPassword(), grantedAuthorities);
        return UserDetailsImpl.build(user);
    }
}