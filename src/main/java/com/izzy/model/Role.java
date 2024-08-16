package com.izzy.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "user_id")
    private List<Long> userIds;

    public Role() {
    }

    public Role(String name){
        this(name, null);
    }

    public Role(String name, List<Long> userIds){
        this.name = name;
        this.userIds = userIds;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getUsers() {
        return userIds;
    }
}