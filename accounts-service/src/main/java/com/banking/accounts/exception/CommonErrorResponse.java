package com.banking.accounts.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class CommonErrorResponse {
	
	private LocalDateTime dateTime;
	private int status;
	private String error;
	private String message;
	private String path;
	
	
	public CommonErrorResponse(LocalDateTime dateTime, int status, String error, String message, String path) {
		super();
		this.dateTime = dateTime;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
		
	}


	public LocalDateTime getDateTime() {
		return dateTime;
	}


	public int getStatus() {
		return status;
	}


	public String getError() {
		return error;
	}


	public String getMessage() {
		return message;
	}


	public String getPath() {
		return path;
	}


}
