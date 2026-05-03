package com.banking.customer.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.banking.customer.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundData(CustomerNotFoundException exception
			,HttpServletRequest httpRequest){
		
		ErrorResponse err = new ErrorResponse(
                LocalDateTime.now(),
                "CUSTOMER_NOT_FOUND",
                exception.getMessage(),
                httpRequest.getRequestURI()
        );
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
		
		
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidations(MethodArgumentNotValidException exception,
			HttpServletRequest httpServletRequest ){
		
		String msg= exception.getBindingResult().getFieldError().getDefaultMessage();
		
		ErrorResponse errorResponse= new ErrorResponse(LocalDateTime.now(),
				"VALIDATION_FAILED",
				msg,
				httpServletRequest.getRequestURI());
				return ResponseEntity.badRequest().body(errorResponse);
		
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalErrors(Exception exception,
			HttpServletRequest httpServletRequest){
		ErrorResponse errorResponse= new ErrorResponse(LocalDateTime.now(),
				"SERVER_ERROR OCCURED",
				exception.getMessage(),
				httpServletRequest.getRequestURI());
		
				return ResponseEntity.internalServerError().body(errorResponse);
		
	}
	
	@ExceptionHandler(UserAlreadyExists.class)
	public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExists exception,
			HttpServletRequest httpServletRequest){
		ErrorResponse errorResponse= new ErrorResponse(LocalDateTime.now(),
				"User Already Exists",
				exception.getMessage(),
				httpServletRequest.getRequestURI());
		
				return ResponseEntity.internalServerError().body(errorResponse);
		
	}
	
	

}
