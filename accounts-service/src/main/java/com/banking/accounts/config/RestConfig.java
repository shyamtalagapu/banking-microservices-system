package com.banking.accounts.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
//	WHY @LoadBalanced ?
//
//			
//
//			Without it:
//
//			RestTemplate understands URLs only
//
//			With it:
//
//			RestTemplate understands SERVICE NAMES
//
//			Spring intercepts request → asks Eureka → resolves instance.
	
	
}