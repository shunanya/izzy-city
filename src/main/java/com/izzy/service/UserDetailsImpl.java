package com.izzy.service;

import com.izzy.model.UserEntity;
import com.izzy.model.ZoneEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private  String first_name;
    private String last_name;
    private String password;
    private String phone_number;
    private String gender;
    private LocalDate date_of_birth;
    private ZoneEntity zone;
    private String shift;
    private UserEntity created_by;
    private Timestamp created_at;
    private UserEntity head_for_user;
    private Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(Long id, String first_name, String last_name, String password, String phone_number,
                            String gender, LocalDate date_of_birth, ZoneEntity zone, String shift,
                            UserEntity created_by, Timestamp created_at,
                            UserEntity head_for_user, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.phone_number  = phone_number;
        this.gender = gender;
        this.date_of_birth = date_of_birth;
        this.zone = zone;
        this.shift = shift;
        this.created_by = created_by;
        this.created_at = created_at;
        this.head_for_user = head_for_user;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserEntity user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName(), user.getLastName(), user.getPassword(), user.getPhoneNumber(),
                user.getGender(), user.getDateOfBirth(), user.getZone(), user.getShift(),
                user.getCreatedBy(), user.getCreatedAt(),
                user.getHeadForUser(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    public String getPhoneNumber(){
        return phone_number;
    }
}
