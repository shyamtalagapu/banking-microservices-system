package com.banking.transactiondetails.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.banking.common.events.constants.KafkaTopics;
import com.banking.common.events.events.MoneyDepositedEvent;
import com.banking.transactiondetails.service.TransactionProcessor;
import com.banking.transactiondetails.service.TransactionService;

import jakarta.annotation.PostConstruct;

/**
 * 
 */
@Component
public class AccountEventConsumer {
	
	private static final Logger LOGGER= LoggerFactory.getLogger(AccountEventConsumer.class);
//    private final TransactionService transactionService;
	private final TransactionProcessor transactionProcessor;


	public AccountEventConsumer(TransactionProcessor transactionProcessor) {
	super();
	this.transactionProcessor = transactionProcessor;
}

	@KafkaListener(  topics = KafkaTopics.ACCOUNT_EVENTS,
	        groupId = "transaction-details-group"
	        )

 public void consume(MoneyDepositedEvent event) {

	 LOGGER.info("Received MoneyDepositedEvent for account: "
         + event.getAccountId() + "  And Deposit Amount : "+event.getAmount()
     );
	 
	 transactionProcessor.processDeposit(event);
 }
	
	@PostConstruct
	public void inspectEventClass() {

	    LOGGER.info("MoneyDepositedEvent loaded from: {}",
	            MoneyDepositedEvent.class
	                    .getProtectionDomain()
	                    .getCodeSource()
	                    .getLocation());

	    for (var constructor :
	            MoneyDepositedEvent.class.getDeclaredConstructors()) {

	        LOGGER.info("MoneyDepositedEvent constructor: {}",
	                constructor);
	    }
	}

}
