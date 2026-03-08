package com.banking.customer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.customer.entity.Customer;
import com.banking.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}
	
	@PostMapping("/saveCustomer")
	public Customer createCustomer(@Valid @RequestBody Customer customer) {
		return customerService.saveCustomer(customer);
	}
	
	@GetMapping("/customer/{id}")
	public Customer fetchCustomerDetails(@PathVariable("id") Long id) {
		return customerService.getCustomer(id);
	}
	
	
}
