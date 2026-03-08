package com.banking.accounts.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.banking.accounts.entity.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long>{
	
	Optional<Account> findByCustomerId(Long customerId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Account a where a.customerId= :customerId")
	Account findByIdOrUpdate(Long customerId);
}
