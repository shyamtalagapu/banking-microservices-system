package com.banking.customer.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public class AuthRegisterRequest {

	@NotEmpty(message = "UserName should not be empty")
	private String userName;
	@NotEmpty(message = "Email shuld not be empty")
	private String email;
	private String password;
	@NotEmpty(message="Role is required")
	private String role;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "AuthRegisterRequest [userName=" + userName + ", email=" + email + ", password=" + password + ", role="
				+ role + "]";
	}
	
	
	
}
