package com.banking.accounts.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banking.accounts.entity.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long>{
	
	List<Account> findByCustomerId(Long customerId);
	Optional<Account> findByAccountId(Long accountId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Account a where a.accountId= :accountId")
	Account findByIdOrUpdate(Long accountId);
	
	@Query("SELECT a.id FROM Account a WHERE a.customerId = :customerId")
	List<Long> getAccountIdsByCustomerId(@Param("customerId") Long customerId);
	
	
	
}
