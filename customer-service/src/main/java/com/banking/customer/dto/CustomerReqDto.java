package com.banking.customer.dto;

import jakarta.validation.constraints.NotEmpty;


public class CustomerReqDto {
		@NotEmpty(message = "Customer Name should not be empty")
		private String fullName;
		
		@NotEmpty(message = "Customer email is required")
		private String email;

		@NotEmpty(message = "Phone number is mandatory for Saving")
		 private String phone;

//		@NotEmpty(message = "Please specify the role")
	    private String role;
//		@NotEmpty(message = "Password is required")
		private String password;

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		
		
		
		
}
