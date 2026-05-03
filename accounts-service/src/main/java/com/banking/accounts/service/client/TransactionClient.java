package com.banking.accounts.service.client;

import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.banking.accounts.service.dto.TransactionRequest;
import com.banking.accounts.service.dto.TransactionResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.spring6.fallback.FallbackMethod;

@Service
public class TransactionClient {
	private final RestTemplate restTemplate;
	
	private static final Logger LOGGER= LoggerFactory.getLogger(TransactionClient.class);

	public TransactionClient(RestTemplate restTemplate) {
		
		this.restTemplate = restTemplate;
	}
//	RestTemplate
//	   ↓
//	LoadBalancerInterceptor
//	   ↓
//	Eureka Client
//	   ↓
//	Fetch service instances
//	   ↓
//	Choose instance (Round Robin)
//	   ↓
//	Replace URL
//	   ↓
//	HTTP call executed
	
	  private final String BASE_URL =
	            "http://TRANSACTIONDETAILS-SERVICE/transactions";
	  

	  @CircuitBreaker(
			  name="transactionservice",
			  fallbackMethod ="transactionFallBack")
		public TransactionResponse createPending(TransactionRequest request) {
		  return restTemplate.postForObject(BASE_URL+ "/pending", request, TransactionResponse.class);
	  }
	  public void markSuccess(Long id) {
	        restTemplate.put(BASE_URL + "/" + id + "/success", null);
	  }

	  public void markFailed(Long id) {
	        restTemplate.put(BASE_URL + "/" + id + "/failed", null);
	  }
	  public void markTransactionCompensated(Long id) {
		  restTemplate.put(BASE_URL + "/" + id + "/compensated", null);
	  }
	  
	  public TransactionResponse transactionFallBack(TransactionRequest request, Exception exception) {
		  LOGGER.info("Oops!.... Transaction service down... FallBack Triggered");
		  TransactionResponse response=new TransactionResponse();
		  response.setMessage("Service Down..");
		  response.setStatus("FAILED");
		  return response;
	  }
	

}
