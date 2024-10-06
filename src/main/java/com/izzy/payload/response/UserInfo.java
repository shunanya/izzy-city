package com.izzy.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.model.Zone;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
        "id",
        "firstName",
        "lastName",
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String zone;
    private String shift;
    private Long createdBy;
    private Timestamp createdAt;
    private UserInfo userManager;
    private List<String> roles;

    public UserInfo(){}
    public UserInfo(@NonNull User user, @Nullable User userManager, Boolean shortInfo) {
        if (shortInfo != null && !shortInfo) {
            this.lastName = user.getLastName();
            this.gender = user.getGender();
            this.dateOfBirth = user.getDateOfBirth();
            this.shift = user.getShift();
            this.createdBy = user.getCreatedBy();
            this.createdAt = user.getCreatedAt();
        }
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.phoneNumber = user.getPhoneNumber();
        List<Role> roles = user.getRoles();
//        this.roles = (roles == null || roles.isEmpty())?null:roles.stream().map(Role::getName).collect(Collectors.toList());
        this.roles = user.getRolesName();
        Zone z = user.getZone();
        this.zone = (z != null) ? z.getName() : null;
        if (userManager != null) {
            this.userManager =  new UserInfo(userManager, null, true);
        } else if (user.getUserManager() != null){
            this.userManager =  new UserInfo();
            this.userManager.setId(user.getUserManager());
        }
    }

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public UserInfo getUserManager() {
        return userManager;
    }

    public void setUserManager(UserInfo userManager) {
        this.userManager = userManager;
    }
}
