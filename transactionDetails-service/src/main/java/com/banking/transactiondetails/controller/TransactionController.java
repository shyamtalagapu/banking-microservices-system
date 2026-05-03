package com.banking.transactiondetails.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.transactiondetails.dto.TransferRequest;
import com.banking.transactiondetails.entity.Transaction;
import com.banking.transactiondetails.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/pending")
    public Transaction create(@RequestBody TransferRequest r) {
        return service.createPending(
            r.getFromAccountId(),
            r.getToAccountId(),
            r.getAmount(),
            r.getReferenceId()
        );
    }
    
    
    @PutMapping("/{id}/success")
    public void markSuccess(@PathVariable Long id) {
    	System.out.println("SUCCESS endpoint HIT for id = " + id);
    	service.markSuccess(id);
    }
    
    @PutMapping("/{id}/failed")
    public void markFailed(@PathVariable Long id) {
    	service.markFailed(id);
    }
    @PutMapping("/{id}/compensated")
    public void markTransactionCompensated(@PathVariable Long id) {
    	service.markCompensated(id);
    }
    
    
}

