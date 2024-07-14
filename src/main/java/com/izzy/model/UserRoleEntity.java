package com.izzy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id"),
                @UniqueConstraint(columnNames = "role_id")
        })
public class UserRoleEntity {
    @EmbeddedId
    private UserRoleKey id;
    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @MapsId("role_id")
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    public UserRoleEntity() {
    }

    // getters and setters

    public UserRoleKey getId() {
        return id;
    }

    public void setId(UserRoleKey id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }
}

