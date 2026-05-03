package com.banking.security.dto;

import jakarta.validation.constraints.NotEmpty;

public class LoginRequest {
	@NotEmpty(message = "Email Id is required for Login")
	private String email;
	@NotEmpty(message = "Password is required for Login")
	private String password;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
