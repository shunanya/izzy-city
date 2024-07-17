package com.izzy.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonPropertyOrder({
        "id",
        "firstName",
        "lastName",
        "password",
        "phoneNumber",
        "gender",
        "dateOfBirth",
        "zone",
        "shift",
        "createdBy",
        "createdAt",
        "headForUser",
        "roles"
})
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Basic
    @Column(name = "first_name")
    private String first_name;
    @Basic
    @Column(name = "last_name")
    private String last_name;
    @Basic
    @Column(name = "password", length = 100)
    @JsonIgnore
    private String password;
    @Basic
    @Column(name = "phone_number", nullable = false, length = 100, unique = true)
    private String phone_number;
    @Basic
    @Column(name = "gender", length = 100)
    private String gender;
    private LocalDate date_of_birth;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone", referencedColumnName = "id")
    private Zone zone;
    @Basic
    @Column(name = "shift", length = 100)
    private String shift;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User created_by;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_for_user", referencedColumnName = "id")
    private User head_for_user;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
    }

    // getters and setters

    public  Set<Role> getRoles(){
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String firstName) {
        this.first_name = firstName;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String lastName) {
        this.last_name = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone_number = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return date_of_birth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.date_of_birth = dateOfBirth;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public User getCreatedBy() {
        return created_by;
    }

    public void setCreatedBy(User createdBy) {
        this.created_by = createdBy;
    }

    public Timestamp getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.created_at = createdAt;
    }

    public User getHeadForUser() {
        return head_for_user;
    }

    public void setHeadForUser(User headForUser) {
        this.head_for_user = headForUser;
    }
}
