package com.banking.accounts.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TransferRequestDto {
	@NotNull(message = "Sender Account Id should not be null")
	private Long fromAccountId;
	@NotNull(message = "Reciever Account Id should not be null")
    private Long toAccountId;
	@Size(min = 1,message = "Minimum1 rupee is required")
    private BigDecimal amount;
	public Long getFromAccountId() {
		return fromAccountId;
	}
	public void setFromAccountId(Long fromAccountId) {
		this.fromAccountId = fromAccountId;
	}
	public Long getToAccountId() {
		return toAccountId;
	}
	public void setToAccountId(Long toAccountId) {
		this.toAccountId = toAccountId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

    
    
}
