package com.banking.customer.service;

import java.time.LocalDateTime;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.banking.customer.auth.dto.AuthRegisterRequest;
import com.banking.customer.authRegister.AuthFeignClient;
import com.banking.customer.dto.CustomerReqDto;
import com.banking.customer.entity.Customer;
import com.banking.customer.exception.CustomerNotFoundException;
import com.banking.customer.exception.UserAlreadyExists;
import com.banking.customer.repository.CustomerRepository;
import com.banking.customer.responseHandler.CustomerResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service

public class CustomerService {
	
	private final CustomerRepository customerRepository;
	private final AuthFeignClient authFeignClient;

	private static final Logger LOGGER= LoggerFactory.getLogger(CustomerService.class);
	
	
	public CustomerService(CustomerRepository customerRepository, AuthFeignClient authFeignClient) {
		super();
		this.customerRepository = customerRepository;
		this.authFeignClient = authFeignClient;
	}
	
	@Transactional
	public CustomerResponse saveCustomer(CustomerReqDto customerReqDto) {

	    LOGGER.info("Call Service Layer : {}", customerReqDto);

	    if (customerRepository.findByEmail(customerReqDto.getEmail()).isPresent()) {
	        throw new UserAlreadyExists(
	                "Customer already exists with email: " + customerReqDto.getEmail()
	        );
	    }

	 
	    Customer customer = new Customer();
	    customer.setCreatedAt(LocalDateTime.now());
	    customer.setKycVerified(false);
	    customer.setFullName(customerReqDto.getFullName());
	    customer.setEmail(customerReqDto.getEmail());
	    customer.setPhone(customerReqDto.getPhone());
	    customer.setRole("ROLE_CUSTOMER");

	    LOGGER.info("Saving customer Details : {}", customer);

	    Customer savedCustomer = customerRepository.save(customer);

	    LOGGER.info("Saved customer Details : {}", savedCustomer);

	 
	    AuthRegisterRequest authRequest = new AuthRegisterRequest();
	    authRequest.setUserName(savedCustomer.getEmail()); 
	    authRequest.setEmail(savedCustomer.getEmail());
	    authRequest.setRole(savedCustomer.getRole());
	    authRequest.setPassword(customerReqDto.getPassword());

	    LOGGER.info("Saving auth Details : {}", authRequest);

	   
	    try {
	        authFeignClient.registerCustomer(authRequest);
	        LOGGER.info("Saved auth Details : {}", authRequest);

	    } catch (Exception e) {

	        String msg = e.getMessage();

	        if (msg != null && msg.contains("User already exists")) {
	            LOGGER.warn("User already exists in auth-service: {}", authRequest.getEmail());

	        } else {
	           
	            throw new RuntimeException("Auth Service failed: " + msg);
	        }
	    }

	  
	    return new CustomerResponse(
	            savedCustomer.getId(),
	            savedCustomer.getCreatedAt(),
	            "Customer registration successful"
	    );
	}
	
	public Customer getCustomer(Long id) { 
		return customerRepository.findById(id)
				.orElseThrow(()->new CustomerNotFoundException(id));
	}

}
