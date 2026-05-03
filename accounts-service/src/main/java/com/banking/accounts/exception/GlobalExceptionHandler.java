package com.banking.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<String> handleCustomerNotFound(CustomerNotFoundException exception){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}

}
