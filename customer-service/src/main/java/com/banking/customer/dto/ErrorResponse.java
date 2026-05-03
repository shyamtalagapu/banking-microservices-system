package com.banking.customer.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
	
	    private LocalDateTime timestamp;
	    private String code;
	    private String message;
	    private String path;
		public ErrorResponse(LocalDateTime timestamp, String code, String message, String path) {
			super();
			this.timestamp = timestamp;
			this.code = code;
			this.message = message;
			this.path = path;
		}
		public LocalDateTime getTimestamp() {
			return timestamp;
		}
		public String getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
		public String getPath() {
			return path;
		}
	    
}
