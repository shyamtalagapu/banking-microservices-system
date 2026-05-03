package com.banking.customer.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.banking.customer.dto.CustomerReqDto;
import com.banking.customer.entity.Customer;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.repository.CustomerRepository;
import com.banking.customer.responseHandler.CustomerResponse;
import com.banking.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService customerService;
	private final CustomerRepository customerRepository;
	private static final Logger LOGGER=LoggerFactory.getLogger(CustomerController.class);
	
	
	public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
		super();
		this.customerService = customerService;
		this.customerRepository = customerRepository;
	}

	@PostMapping("/saveCustomer")
	public ResponseEntity<CustomerResponse>createCustomer(@Valid @RequestBody CustomerReqDto customerReqDto) {
		CustomerResponse savingResponse= customerService.saveCustomer(customerReqDto); 
		 return ResponseEntity.status(HttpStatus.CREATED).body(savingResponse);
	}
	
	@GetMapping("/customer/{id}")
	public Customer fetchCustomerDetails(@PathVariable("id") Long id) {
		return customerService.getCustomer(id);
	}
	
   
    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }
    
    @DeleteMapping("/admin/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok("Customer deleted");
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Long> getCustomerIdByEmail(@PathVariable String email) {
    	LOGGER.info("Request Reecieved as Email from the Account Service : "+email);
        Customer customer = customerRepository.findByEmail(email)
               .orElseThrow(()->new CustomerNotFoundException(email));
        LOGGER.info("CustomerDetails : "+customer);
        return ResponseEntity.ok(customer.getId());
    }
	
	
}
