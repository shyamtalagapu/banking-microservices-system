package com.banking.transactiondetails.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banking.transactiondetails.model.TransactionStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_records")
public class Transaction {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long fromAccountId;
	    private Long toAccountId;

	    private BigDecimal amount;   

	    @Enumerated(EnumType.STRING)
	    private TransactionStatus status; // PENDING / SUCCESS / FAILED

	    private String referenceId;

	    private LocalDateTime createdAt;

	    public Transaction() {}

	    public Transaction(Long from,
	                             Long to,
	                             BigDecimal amount,
	                             String referenceId) {
	        this.fromAccountId = from;
	        this.toAccountId = to;
	        this.amount = amount;
	        this.status = TransactionStatus.PENDING;
	        this.referenceId = referenceId;
	        this.createdAt = LocalDateTime.now();
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

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

		

		public TransactionStatus getStatus() {
			return status;
		}

		public void setStatus(TransactionStatus status) {
			this.status = status;
		}

		public String getReferenceId() {
			return referenceId;
		}

		public void setReferenceId(String referenceId) {
			this.referenceId = referenceId;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
	    
	    
	    
	    
	
}
