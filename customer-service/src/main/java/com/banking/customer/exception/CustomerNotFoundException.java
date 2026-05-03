package com.banking.customer.exception;

public class CustomerNotFoundException extends RuntimeException{

	public CustomerNotFoundException(Long id) {
		super("customer not found with id : "+id);
		// TODO Auto-generated constructor stub
	}
	public CustomerNotFoundException(String email) {
		super("customer not found with email : "+email);
		// TODO Auto-generated constructor stub
	}
	

}
