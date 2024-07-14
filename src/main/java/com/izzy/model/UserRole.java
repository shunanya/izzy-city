package com.izzy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id"),
                @UniqueConstraint(columnNames = "role_id")
        })
public class UserRole {
    @EmbeddedId
    private UserRoleKey id;
    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @MapsId("role_id")
    @JoinColumn(name = "role_id")
    private Role role;

    public UserRole() {
    }

    // getters and setters

    public UserRoleKey getId() {
        return id;
    }

    public void setId(UserRoleKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

