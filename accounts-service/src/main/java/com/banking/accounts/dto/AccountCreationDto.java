package com.banking.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class AccountCreationDto {
	
	@NotNull(message = "CustomerId should not be NULL")
	private Long customerId;
	
	@NotNull(message = "Account Id should not be NULL")
	private long accountId;
	
	@NotEmpty(message = "Account Name should not be empty")
	private String accountName;
	
	@NotEmpty(message = "IFSC code should not be empty")
	private String ifscCode;
	
	@NotEmpty(message = "Account branch should not be empty")
	private String bankBranch;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	
	
	

	
	
	
	
	

}
