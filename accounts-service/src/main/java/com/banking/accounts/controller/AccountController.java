package com.banking.accounts.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.accounts.dto.AccountCreationDto;
import com.banking.accounts.dto.AccountCreationResponse;
import com.banking.accounts.dto.TransferRequestDto;
import com.banking.accounts.entity.Account;
import com.banking.accounts.security.JwtUtil;
import com.banking.accounts.service.AccountService;
import com.banking.accounts.service.dto.TransactionResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {
	
	private final AccountService accountService;
	private final JwtUtil jwtUtil;

		
		public AccountController(AccountService accountService, JwtUtil jwtUtil) {
			super();
			this.accountService = accountService;
			this.jwtUtil = jwtUtil;
		}
		private static final Logger LOGGER=LoggerFactory.getLogger(AccountService.class);
	
		@PostMapping("/create")
		public ResponseEntity<AccountCreationResponse>  createNewAccount(@Valid @RequestBody AccountCreationDto accountCreationDto) {
//			return accountService.createAccount(customerId);
			// Account Controller started 
			LOGGER.info("Request Received: "+accountCreationDto.getCustomerId());
			AccountCreationResponse response= accountService.createAccount(accountCreationDto);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
			
		}
	   
	
	 	@PostMapping("/{id}/credit")
	    public void credit(@PathVariable("id") Long id,
	                       @RequestParam BigDecimal amount) {
		 accountService.creditAmmount(id, amount);
	    }

	    @PostMapping("/{id}/debit")
	    public void debit(@PathVariable Long id,
	                      @RequestParam BigDecimal amount) {
	    	accountService.debitAmount(id, amount);
	    }
	    
	    @GetMapping("/checkBalance/{id}")
	    public Account getBalance(@PathVariable Long id) {
	        return accountService.fetchAccountDetails(id);
	    }
	    
	    
	    @PostMapping("/transferMoney")
	    public ResponseEntity<TransactionResponse> transferMoney(@RequestBody TransferRequestDto transferRequestDto){
	    	TransactionResponse response= accountService.transferAmmountSagaImplementation(transferRequestDto.getFromAccountId(), 
	    			transferRequestDto.getToAccountId(), 
	    			transferRequestDto.getAmount());
	    	return ResponseEntity.ok(response);
	    	 
	    	
	    }

	   
	    @GetMapping("/myAccounts")
	    public ResponseEntity<List<Account>> getMyAccounts(
	            @RequestHeader("Authorization") String authHeader) {
	    	LOGGER.info("Request Received for List Of Accounts ");
	    	LOGGER.info("Authorization: "+authHeader);
	    	
	        String token = authHeader.substring(7);
	        
	        LOGGER.info("Token : "+token);

	        String email = jwtUtil.extractUserName(token);
	        LOGGER.info("EMAIL : "+email);
	        List<Account> accounts= accountService.getAccountsByEmail(email);
	        if (accounts.size()>0) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(accounts);
			}
	        else {
	        	 return ResponseEntity.status(HttpStatus.NO_CONTENT).build();	
	        }
	       
	    }
	    
	    @PostMapping("/approve/{accountId}")
	    public ResponseEntity<Account> approve(@PathVariable Long accountId) {
	        return ResponseEntity.ok(accountService.approveAccount(accountId));
	    }
	    
	    @DeleteMapping("/admin/accounts/{id}")
	    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
	    	accountService.deleteAccount(id);
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Account deleted successfully");
	    }
	    
}
