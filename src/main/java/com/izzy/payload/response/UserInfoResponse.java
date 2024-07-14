package com.izzy.payload.response;

import java.util.List;

public class UserInfoResponse {
	private Long id;
	private String username;
	private String phone_number;
	private List<String> roles;

	public UserInfoResponse(Long id, String username, String phoneNumber, List<String> roles) {
		this.id = id;
		this.username = username;
		this.phone_number = phoneNumber;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phone_number;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phone_number = phoneNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}
}