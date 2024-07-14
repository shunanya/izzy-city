package com.izzy.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refreshtoken")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "expiry_date", nullable = false)
    private Instant expiry_date;
    @Column(name = "current_token", nullable = false, unique = true)
    private String current_token;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public RefreshToken() {
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExpiryDate() {
        return expiry_date;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiry_date = expiryDate;
    }

    public String getCurrentToken() {
        return current_token;
    }

    public void setCurrentToken(String currentToken) {
        this.current_token = currentToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}