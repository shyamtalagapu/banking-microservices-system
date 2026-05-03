package com.banking.accounts.entity;

import java.math.BigDecimal;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "accountDetails")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private Long accountId;
	private Long customerId;
	@Column(nullable = false)
	private BigDecimal balance;
	private String accountStatus;
	private LocalDateTime createdAt;
	
	private String accountName;
	
	private String IFSCCode;
	
	private String bankBranch;

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Account(Long customerId,Long accountId, String accountName, String IFSCCode, String bankBranch) {
		super();
		    this.customerId = customerId;
		    this.accountId= accountId;
		    this.balance = BigDecimal.ZERO;
		    this.accountStatus = "INACTIVE";
		    this.createdAt = LocalDateTime.now();
		    this.accountName=accountName;
		    this.IFSCCode=IFSCCode;
		    this.bankBranch=bankBranch;
	}

	
	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
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

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getIFSCCode() {
		return IFSCCode;
	}

	public void setIFSCCode(String iFSCCode) {
		IFSCCode = iFSCCode;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	
	

	
}
