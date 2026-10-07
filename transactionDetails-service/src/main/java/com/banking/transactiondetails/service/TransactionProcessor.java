package com.banking.transactiondetails.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.banking.common.events.events.MoneyDepositedEvent;

@Service
public class TransactionProcessor {
	
	private static final Logger LOGGER= LoggerFactory.getLogger(TransactionProcessor.class);
	
	public void processDeposit(MoneyDepositedEvent event) {

        LOGGER.info(
                "Processing deposit transaction for account {} with amount {}",
                event.getAccountId(),
                event.getAmount()
        );

        if (event.getAccountId() == null) {
            throw new IllegalArgumentException(
                    "Account ID cannot be null"
            );
        }

        if (event.getAmount() == null ||
                event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero"
            );
        }

        String transactionReference =
                "TXN-" + UUID.randomUUID();

        LOGGER.info(
                "Deposit transaction processed successfully. " +
                "Reference={}, AccountId={}, Amount={}, Type=DEPOSIT",
                transactionReference,
                event.getAccountId(),
                event.getAmount()
        );
    }
	
}
