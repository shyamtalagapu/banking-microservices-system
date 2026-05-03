package com.banking.security.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;


@Entity
@Table(name="usermaster")
public class UserMaster {
	
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long userId;
@Column(unique = true, nullable = false)
private String userName;

@Email
@Column(unique = true)
private String email;
private String password;
private String role;
public Long getUserId() {
	return userId;
}
public void setUserId(Long userId) {
	this.userId = userId;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
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
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
}
@Override
public String toString() {
	return "UserMaster [userId=" + userId + ", userName=" + userName + ", email=" + email + ", password=" + password
			+ ", role=" + role + "]";
}
	

}
