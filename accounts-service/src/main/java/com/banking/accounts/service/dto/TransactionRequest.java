package com.banking.accounts.service.dto;

import java.math.BigDecimal;

public class TransactionRequest {
	

	    private Long fromAccountId;
	    private Long toAccountId;
	    private BigDecimal amount;
	    private String referenceId;
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
		public String getReferenceId() {
			return referenceId;
		}
		public void setReferenceId(String referenceId) {
			this.referenceId = referenceId;
		}

	    // getters setters
	

}
