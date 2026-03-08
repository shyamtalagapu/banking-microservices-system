package com.banking.customer.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="customerdetails")
public class Customer {
	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	private long id;
	
	@NotBlank
	private String fullName;
	
	@Email
	@Column( unique = true)
	private String email;

    @Pattern(regexp = "\\d{10}")
	 private String phone;

    private boolean kycVerified;

    private LocalDateTime createdAt;
    
    
    

	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public Customer(long id, @NotBlank String fullName, @Email String email, @Pattern(regexp = "\\d{10}") String phone,
			boolean kycVerified, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.kycVerified = kycVerified;
		this.createdAt = createdAt;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public boolean isKycVerified() {
		return kycVerified;
	}

	public void setKycVerified(boolean kycVerified) {
		this.kycVerified = kycVerified;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
    
    

}
