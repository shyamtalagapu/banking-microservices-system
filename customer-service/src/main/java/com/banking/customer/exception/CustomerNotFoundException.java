package com.banking.customer.exception;

public class CustomerNotFoundException extends RuntimeException{

	public CustomerNotFoundException(Long id) {
		super("customer not found with id : "+id);
		// TODO Auto-generated constructor stub
	}
	

}
