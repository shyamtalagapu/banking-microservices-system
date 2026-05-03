package com.banking.accounts.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("customer-service")
public interface CustomerFeign {

	@GetMapping("/customers/email/{email}")
    Long getCustomerIdByEmail(@PathVariable String email);	
}

