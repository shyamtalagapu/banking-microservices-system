package com.banking.accounts.service;

import java.math.BigDecimal;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import com.banking.accounts.entity.Account;
import com.banking.accounts.exception.AccountDetailsNotFound;
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

	
	
	public AccountService(AccountRepository accountRepository, TransactionClient transactionClient) {
		this.accountRepository = accountRepository;
		this.transactionClient = transactionClient;
	}

	@Transactional
    public Account createAccount(Long accountId) {
        return accountRepository.save(new Account(accountId));
    }

	@Transactional
	public void creditAmmount(Long accountId, BigDecimal amount) {
		Account accountDetails= accountRepository.findByIdOrUpdate(accountId);
		accountDetails.setBalance(accountDetails.getBalance().add(amount));
	}
	
	@Transactional
	public void debitAmount(Long accountId, BigDecimal amount) {
		Account accountDetails= accountRepository.findByIdOrUpdate(accountId);
		if (accountDetails.getBalance().compareTo(amount)<0) {
			throw new InsufficientBalanceException();
		}
		accountDetails.setBalance(accountDetails.getBalance().subtract(amount));
		
	}
	
	public Account fetchAccountDetails(Long accountId) {
		Account account= accountRepository.findByCustomerId(accountId).
				orElseThrow(()->new AccountDetailsNotFound());
		return account;
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
