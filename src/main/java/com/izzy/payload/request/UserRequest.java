package com.izzy.payload.request;

import jakarta.validation.constraints.Size;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;

public class UserRequest {
    @Size(min = 3, max = 20)
    private String firstName;
    private String lastName;
    @Size(max = 50)
    private String phoneNumber;
    @Size(min = 6, max = 40)
    private String password;
    private String gender;
    private LocalDate date_of_birth;
    private String zone;
    private String shift;
    private Long created_by;
    private Timestamp created_at;
    private Long head_for_user;
    private Set<String> role;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Long getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Long created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Long getHead_for_user() {
        return head_for_user;
    }

    public void setHead_for_user(Long head_for_user) {
        this.head_for_user = head_for_user;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }
}
