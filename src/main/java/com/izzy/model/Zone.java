package com.izzy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "zones")
public class Zone {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Zone() {
    }

    public Zone(String name) {
        this.name = name;
    }

    public Zone(Long id, String name) {
        this.id = id;
        this.name = name;
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
}