package com.banking.transactiondetails.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.transactiondetails.entity.Transaction;

public interface TransactionDetailsRepository extends JpaRepository<Transaction,Long> {

}
