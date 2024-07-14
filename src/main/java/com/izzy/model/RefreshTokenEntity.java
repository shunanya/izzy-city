package com.izzy.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "refreshtoken")
public class RefreshTokenEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiry_date;
    @Column(name = "current_token", nullable = false, unique = true)
    private String current_token;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    public RefreshTokenEntity() {
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getExpiryDate() {
        return expiry_date;
    }

    public void setExpiry_date(Timestamp expiryDate) {
        this.expiry_date = expiryDate;
    }

    public String getCurrentToken() {
        return current_token;
    }

    public void setCurrent_token(String currentToken) {
        this.current_token = currentToken;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}