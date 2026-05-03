package com.banking.customer.responseHandler;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;

public class CustomerResponse {
	private Long id;
	private LocalDateTime createdAt;
    private String message;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public CustomerResponse(Long id, LocalDateTime createdAt, String message) {
		super();
		this.id = id;
		this.createdAt = createdAt;
		this.message = message;
	}
  
    
}
