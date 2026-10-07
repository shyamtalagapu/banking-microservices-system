package com.banking.accounts.kafkaproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.banking.common.events.constants.KafkaTopics;
import com.banking.common.events.events.MoneyDepositedEvent;

@Component
public class AccountEventProducer {
	private static final Logger LOGGER= LoggerFactory.getLogger(AccountEventProducer.class);
	
	private final KafkaTemplate<String, MoneyDepositedEvent>  kafkaTemplate;

	public AccountEventProducer(KafkaTemplate<String, MoneyDepositedEvent> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void publishMoneyDepositedEvent(MoneyDepositedEvent event) {

	    LOGGER.info(
	        "Publishing MoneyDepositedEvent: accountId={}, amount={}",
	        event.getAccountId(),
	        event.getAmount()
	    );

	    kafkaTemplate.send(
	            KafkaTopics.ACCOUNT_EVENTS,
	            String.valueOf(event.getAccountId()),
	            event
	    ).whenComplete((result, ex) -> {

	        if (ex != null) {

	            LOGGER.error(
	                "FAILED to publish MoneyDepositedEvent: accountId={}",
	                event.getAccountId(),
	                ex
	            );

	        } else {

	            LOGGER.info(
	                "SUCCESS: MoneyDepositedEvent published: topic={}, partition={}, offset={}",
	                result.getRecordMetadata().topic(),
	                result.getRecordMetadata().partition(),
	                result.getRecordMetadata().offset()
	            );
	        }
	    });
	}
}
