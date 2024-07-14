package com.izzy.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRoleKey implements Serializable {
    private Long user_id;
    private Long role_id;

    public UserRoleKey() {
    }

    // getters and setters, equals, and hashCode

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long userId) {
        this.user_id = userId;
    }

    public Long getRoleId() {
        return role_id;
    }

    public void setRoleId(Long roleId) {
        this.role_id = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleKey that = (UserRoleKey) o;
        return Objects.equals(user_id, that.user_id) && Objects.equals(role_id, that.role_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, role_id);
    }
}
