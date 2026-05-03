package com.banking.accounts.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;
import javax.security.auth.login.AccountNotFoundException;

import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.banking.accounts.config.CustomerFeign;
import com.banking.accounts.dto.AccountCreationDto;
import com.banking.accounts.dto.AccountCreationResponse;
import com.banking.accounts.entity.Account;
import com.banking.accounts.exception.AccountDetailsNotFound;
import com.banking.accounts.exception.CustomerNotFoundException;
import com.banking.accounts.exception.InsufficientBalanceException;
import com.banking.accounts.repository.AccountRepository;
import com.banking.accounts.service.client.TransactionClient;
import com.banking.accounts.service.dto.TransactionRequest;
import com.banking.accounts.service.dto.TransactionResponse;

import jakarta.transaction.Transactional;

@Service
public class AccountService {
	
	private static final Logger LOGGER= LoggerFactory.getLogger(AccountService.class);
	
	private final AccountRepository accountRepository;
	private final TransactionClient transactionClient;
	private final RestTemplate restTemplate;
	private final CustomerFeign customerFeign;


	

	public AccountService(AccountRepository accountRepository, TransactionClient transactionClient,
			RestTemplate restTemplate, CustomerFeign customerFeign) {
		super();
		this.accountRepository = accountRepository;
		this.transactionClient = transactionClient;
		this.restTemplate = restTemplate;
		this.customerFeign = customerFeign;
	}

	@Transactional
    public AccountCreationResponse createAccount(AccountCreationDto accountCreationDto) {
		// Validating the customer
		String url= "http://customer-service/customers/customer/"+accountCreationDto.getCustomerId();
		
		try {
			Object response= restTemplate.getForObject(url,Object.class);
			LOGGER.info("Service Call TO CUSTOMER Srvice"+response);
		} catch (Exception e) {
			throw new CustomerNotFoundException("Customer not found with this Id :"+accountCreationDto.getCustomerId());
		}
		Account account= new Account(accountCreationDto.getCustomerId(),accountCreationDto.getAccountId(), accountCreationDto.getAccountName(),accountCreationDto.getIfscCode(), accountCreationDto.getBankBranch());	
		LOGGER.info("Acount: "+account);
		accountRepository.save(account);
		AccountCreationResponse accountCreationResponse= new AccountCreationResponse(accountCreationDto.getAccountId(),accountCreationDto.getCustomerId(),
				account.getBalance(),account.getAccountStatus(),account.getCreatedAt());
		return accountCreationResponse;
    }

	@Transactional
	public void creditAmmount(Long accountId, BigDecimal amount) {
		Account accountDetails= accountRepository.findByIdOrUpdate(accountId);
		if (accountDetails.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
			accountDetails.setBalance(accountDetails.getBalance().add(amount));
		}else {
			throw new RuntimeException("Please Activate the account to deposit the amount");	
		}
		
		
	}
	
	@Transactional
	public void debitAmount(Long accountId, BigDecimal amount) {
		Account accountDetails= accountRepository.findByIdOrUpdate(accountId);
		if (accountDetails.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
			if (accountDetails.getBalance().compareTo(amount)<0) {
				throw new InsufficientBalanceException();
			}
			accountDetails.setBalance(accountDetails.getBalance().subtract(amount));
		}else {
			throw new RuntimeException("Please Activate the account to debit the amount");
		}
		
		
		
	}
	
	public Account fetchAccountDetails(Long accountId) {
		Account account= accountRepository.findByAccountId(accountId).
				orElseThrow(()->new AccountDetailsNotFound());
		return account;
	}
	
	public List<Account> getAccountsByEmail(String email) {

	    // call customer-service OR map email → customerId
	    Long customerId = getCustomerIdFromEmail(email);
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
	    List<Account> activeAccounts= accounts.stream().filter(account->account.getAccountStatus()
	    		.equals("ACTIVE")).
	    collect(Collectors.toList());
	    return activeAccounts;
	}
	private Long getCustomerIdFromEmail(String email) {
	    return customerFeign.getCustomerIdByEmail(email);
	}
	
    public Account approveAccount(Long accountId) {

		Account account= accountRepository.findByAccountId(accountId).
				orElseThrow(()->new AccountDetailsNotFound());
               
        if ("ACTIVE".equalsIgnoreCase(account.getAccountStatus())) {
            throw new RuntimeException("Account is already active");
        }

        account.setAccountStatus("ACTIVE");

        return accountRepository.save(account);
    }
   
    public void deleteAccount(Long accountId) {

        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountDetailsNotFound());

        accountRepository.delete(account);
    }
	/*
	 * @Transactional public void transferMoney(Long fromId, Long toId, BigDecimal
	 * amount) { if (fromId.equals(toId)) { throw new
	 * RuntimeException("Cannot Transfer to Same Account "); } //Prevents
	 * deadLocking Long first = Math.min(fromId, toId); Long second =
	 * Math.max(fromId, toId); Account accountA=
	 * accountRepository.findByIdOrUpdate(first); Account accountB=
	 * accountRepository.findByIdOrUpdate(second);
	 * 
	 * if (accountA.getBalance().compareTo(amount)<0) { throw new
	 * InsufficientBalanceException(); }
	 * accountA.setBalance(accountA.getBalance().subtract(amount));
	 * accountB.setBalance(accountB.getBalance().add(amount)); }
	 * 
	 */
	
	
	public void compensateDebit(Long fromAccntId, BigDecimal amount) {
		try {
			LOGGER.warn("Compensating debit for account {}", fromAccntId);
			Account account= accountRepository.findById(fromAccntId).orElseThrow();
			account.setBalance(account.getBalance().add(amount));
			accountRepository.save(account);
			LOGGER.warn("Compensated sucessfully for account {}", fromAccntId);

		}
		catch (Exception e) {
			LOGGER.error("compensate debit exception : "+e.getMessage());
		}
	}
	
	@Transactional
	public TransactionResponse transferAmmountSagaImplementation(Long fromId, Long toId, BigDecimal amount) {
		LOGGER.info("Saga Started. Transaction PROCESS: ");
		//creating a transfer request (Ledger entry)
		TransactionRequest request= new TransactionRequest();
		request.setFromAccountId(fromId);
		request.setToAccountId(toId);
		request.setAmount(amount);
		request.setReferenceId(UUID.randomUUID().toString());
		LOGGER.info("SAGA CONTINUED ");
		
		
	    // Call transaction-service → create PENDING record
		
		TransactionResponse response= transactionClient.createPending(request);
		try {
			LOGGER.info("Saga Started. Transaction ID: "
                + response.getId());
			 
			 debitAmount(fromId, amount);
			 LOGGER.info("Saga worked. Debit Successfull ID: "
		                + response.getId());
			 creditAmmount(toId,amount);
			 LOGGER.info("Saga worked. credit Successfull ID: "
		                + response.getId());
			 //making the transaction successfull
			 transactionClient.markSuccess(response.getId());
			 LOGGER.info("Saga worked. update Successfull ID: "
		                + response.getId());
			 return new TransactionResponse(response.getId(),"SUCCESS",request.getReferenceId(),"Amount Transfer successful");
			 
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Saga failed. Transaction failed: "
	                + response.getId());
			transactionClient.markFailed(response.getId());
			LOGGER.error("Saga failed. updated with fail: "
	                + response.getId());
			//Reverse Debit
			compensateDebit(fromId, amount);
			transactionClient.markTransactionCompensated(response.getId());
			// IMPORTANT → rethrow so DB rollback happens
			return new TransactionResponse(
	                response.getId(),
	                "FAILED",
	                request.getReferenceId(),
	                e.getMessage()
	        );
		}

		
		
	}
	
	

}
