package com.izzy.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        "rolesName"
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "password", length = 100)
    @JsonIgnore
    private String password;
    @Column(name = "phone_number", nullable = false, length = 100, unique = true)
    private String phoneNumber;
    @Column(name = "gender", length = 100)
    private String gender;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone", referencedColumnName = "id")
    private Zone zone;
    @Column(name = "shift", length = 100)
    private String shift;
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());
    @Column(name = "user_manager")
    private Long userManager;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
    @Transient
    private List<String> rolesName;

    public User() {
    }

    public User(Long id, String firstName, String phoneNumber) {
        this(id, firstName, phoneNumber, null, null, null, null);
    }

    public User(Long id, String firstName, String phoneNumber, String gender, Zone zone, String shift, List<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.zone = zone;
        this.shift = shift;
        this.roles = roles;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void loadRoleNames() {
        if (roles != null && !roles.isEmpty())
            rolesName = roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserManager() {
        return userManager;
    }

    public void setUserManager(Long userManager) {
        this.userManager = userManager;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<String> getRolesName() {
        return rolesName;
    }

    public void setRolesName(List<String> rolesName) {
        this.rolesName = rolesName;
    }
}
