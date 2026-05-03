package com.banking.customer.authRegister;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banking.customer.auth.dto.AuthRegisterRequest;

@FeignClient(name = "auth-service", url = "http://localhost:8083")
public interface AuthFeignClient {
	
	@PostMapping("/auth/register")
	void registerCustomer(@RequestBody AuthRegisterRequest registerRequest);

}
