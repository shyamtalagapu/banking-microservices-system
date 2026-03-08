package com.banking.customer.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.banking.customer.entity.Customer;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service

public class CustomerService {
	
	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		super();
		this.customerRepository = customerRepository;
	}
	
	public Customer saveCustomer(Customer customer) {
		customer.setCreatedAt(LocalDateTime.now());
		customer.setKycVerified(false);
		return customerRepository.save(customer);
		
	}
	
	
	public Customer getCustomer(Long id) { 
		return customerRepository.findById(id)
				.orElseThrow(()->new CustomerNotFoundException(id));
	}

}
