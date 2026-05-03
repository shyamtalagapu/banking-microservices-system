package com.banking.accounts.service.dto;

public class TransactionResponse {

	 	private Long id;
	    private String status;
	    private String referenceId;
	    private String message;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getReferenceId() {
			return referenceId;
		}
		public void setReferenceId(String referenceId) {
			this.referenceId = referenceId;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
		public TransactionResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public TransactionResponse(Long id, String status, String referenceId, String message) {
			super();
			this.id = id;
			this.status = status;
			this.referenceId = referenceId;
			this.message = message;
		}
	    
	    
	
}
