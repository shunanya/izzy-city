package com.izzy.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.izzy.model.Role;
import com.izzy.model.User;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonPropertyOrder({
        "id",
        "userName",
        "phoneNumber",
        "roles",
        "zone",
})
public class UserShortInfo implements Serializable {
    private final Long id;
    private final String userName;
    private final String phoneNumber;
    private final List<String> roles;
    private final String zone;

    public UserShortInfo(User user) {
        this.id = user.getId();
        this.userName = user.getFirstName();
        this.phoneNumber = user.getPhoneNumber();
        this.roles = user.getRolesName();
//        Set<Role> role = user.getRoles();
//        List<String> roles = new ArrayList<>(role.size());
//        role.forEach(r -> roles.add(r.getName()));
//        this.roles = roles;
        this.zone = user.getZone().getName();
    }

        public UserShortInfo(@NonNull Long id, @NonNull String username, @NonNull String phoneNumber, @NonNull List<String> roles, String zone) {
            this.id = id;
            this.userName = username;
            this.phoneNumber = phoneNumber;
            this.roles = roles;
            this.zone = zone;
        }

        public UserShortInfo(@NonNull Long id, @NonNull String username, @NonNull String phoneNumber, @NonNull Set<Role> role, String zone) {
            this.id = id;
            this.userName = username;
            this.phoneNumber = phoneNumber;
            List<String> roles = new ArrayList<>(role.size());
            role.forEach(r -> roles.add(r.getName()));
            this.roles = roles;
            this.zone = zone;
        }

    public Long getId() {return id;}

    public String getUserName() {return userName;}

    public String getPhoneNumber() {return phoneNumber;}

    public List<String> getRoles() {return roles;}

    public String getZone() {
        return zone;
    }
}
