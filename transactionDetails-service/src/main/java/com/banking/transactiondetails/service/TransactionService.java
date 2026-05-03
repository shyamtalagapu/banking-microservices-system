package com.banking.transactiondetails.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.banking.transactiondetails.entity.Transaction;
import com.banking.transactiondetails.model.TransactionStatus;
import com.banking.transactiondetails.repository.TransactionDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	
	private static final Logger LOGGER= LoggerFactory.getLogger(TransactionService.class); 

    private final TransactionDetailsRepository repo;

    public TransactionService(TransactionDetailsRepository repo) {
        this.repo = repo;
    }

    public Transaction createPending(
            Long from,
            Long to,
            BigDecimal amount,
            String ref) {

        return repo.save(
            new Transaction(from, to, amount, ref)
        );
    }

    @Transactional
    public void markSuccess(Long id) {
    	
    	LOGGER.info("Received PENDING transaction request");
    	LOGGER.info("Marking transaction {} SUCCESS", id);
        Transaction tx = repo.findById(id).orElseThrow();
        tx.setStatus(TransactionStatus.SUCCESS);
        repo.save(tx);
        LOGGER.info("TRANSACTION STATUS UPDATED SUCCESSFULLY = " + id);
    }
    @Transactional
    public void markFailed(Long id) {
        Transaction tx = repo.findById(id).orElseThrow();
        tx.setStatus(TransactionStatus.FAILED);
        repo.save(tx);
    }
    
    @Transactional
    public void markCompensated(Long id) {
    	LOGGER.info("Received COMPENSATED transaction request");
    	Transaction transaction= repo.findById(id).orElseThrow();
    	transaction.setStatus(TransactionStatus.COMPENSATED);
    	repo.save(transaction);
    	LOGGER.info("TRANSACTION STATUS COMPENSATED SUCCESSFULLY");
    	
    }
}
