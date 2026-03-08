package com.banking.accounts.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banking.accounts.dto.TransferRequestDto;
import com.banking.accounts.entity.Account;
import com.banking.accounts.service.AccountService;
import com.banking.accounts.service.dto.TransactionResponse;

@RestController
@RequestMapping("/accounts")
public class AccountController {
	
	private final AccountService accountService;

		public AccountController(AccountService accountService) {
			super();
			this.accountService = accountService;
		}
	
		@PostMapping("/create/{customerId}")
		public Account createNewAccount(@PathVariable Long customerId) {
			return accountService.createAccount(customerId);
			
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
	    
}
