package com.banking.security.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.banking.security.responseHandlers.ApiErrorrResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiErrorrResponse> handleUserNotFound(UserNotFoundException exception,
			HttpServletRequest httpServletRequest){
		ApiErrorrResponse apiErrorrRespon= new ApiErrorrResponse(
				HttpStatus.NOT_FOUND.value(), "User Not Found",  exception.getMessage(), httpServletRequest.getRequestURI());
		return new ResponseEntity<>(apiErrorrRespon,HttpStatus.NOT_FOUND);
		
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiErrorrResponse> invalidCredentialsException(InvalidCredentialsException exception,
			HttpServletRequest httpServletRequest){
		ApiErrorrResponse apiErrorrRespon= new ApiErrorrResponse(
				HttpStatus.UNAUTHORIZED.value(), "Invalid credentials",  exception.getMessage(), httpServletRequest.getRequestURI());
		return new ResponseEntity<>(apiErrorrRespon,HttpStatus.UNAUTHORIZED);
		
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorrResponse> handleALLExceptions(Exception exception,
			HttpServletRequest httpServletRequest){
		ApiErrorrResponse apiErrorrRespon= new ApiErrorrResponse(
				HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",  exception.getMessage(), httpServletRequest.getRequestURI());
		return new ResponseEntity<>(apiErrorrRespon,HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
		
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorrResponse> handleValidationErrors(MethodArgumentNotValidException exception,
			HttpServletRequest httpServletRequest){
		
		String message=exception.getBindingResult()
				.getFieldErrors().stream()
				.map(error-> error.getField()+" : "+error.getDefaultMessage())
				.findFirst().orElse("Validation Error");
		
		ApiErrorrResponse apiErrorrRespon= new ApiErrorrResponse(
				HttpStatus.BAD_REQUEST.value(), "Bad Request",  exception.getMessage(), httpServletRequest.getRequestURI());
		return new ResponseEntity<>(apiErrorrRespon,HttpStatus.BAD_REQUEST);
		
	}
	
	

}
