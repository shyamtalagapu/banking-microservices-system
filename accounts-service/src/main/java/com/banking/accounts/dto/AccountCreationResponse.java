package com.banking.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;

public class AccountCreationResponse {
	
	private Long id;
	private Long customerId;
	private BigDecimal balance;
	private String accountStatus;
	private LocalDateTime createdAt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public AccountCreationResponse(Long id, Long customerId, BigDecimal balance, String accountStatus,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.balance = balance;
		this.accountStatus = accountStatus;
		this.createdAt = createdAt;
	}
	
	
	
	

}
