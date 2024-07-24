package com.izzy.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.izzy.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final Long id;
    @JsonIgnore
    private final String password;
    private final String phone_number;
    private final Collection<? extends GrantedAuthority> authorities;


    private UserPrincipal(Long id, String first_name, String last_name, String password, String phone_number,
                          String gender, LocalDate date_of_birth, String zone, String shift,
                          Long created_by, Timestamp created_at,
                          Long head_for_user, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.phone_number = phone_number;
        this.authorities = authorities;
    }

    public static UserPrincipal build(@NotBlank User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());

        return new UserPrincipal(
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

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserPrincipal user = (UserPrincipal) o;
        return Objects.equals(id, user.id);
    }
}
