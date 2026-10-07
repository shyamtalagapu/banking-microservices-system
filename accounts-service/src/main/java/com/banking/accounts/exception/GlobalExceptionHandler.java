package com.banking.accounts.exception;

import java.time.LocalDateTime;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<String> handleCustomerNotFound(CustomerNotFoundException exception){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}

	@ExceptionHandler(AccountDetailsException.class)
	public ResponseEntity<CommonErrorResponse>handleAccountDetailsExceptions(AccountDetailsException exception, 
			HttpServletRequest request){
		
		CommonErrorResponse errorResponse= new CommonErrorResponse(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Error Occured",
				exception.getMessage(),
				request.getRequestURI()
				);
	return ResponseEntity.badRequest().body(errorResponse);
   }
}
