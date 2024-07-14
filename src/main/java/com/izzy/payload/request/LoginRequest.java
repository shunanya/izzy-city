package com.izzy.payload.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String password;

    // getters and setters

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
}