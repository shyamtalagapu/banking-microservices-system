package com.banking.common.events.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.banking.common.events.enums.EventType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MoneyDepositedEvent extends BaseEvent {

    private final Long accountId;
    private final BigDecimal amount;

    // Used by Accounts Service to CREATE a new event
    public MoneyDepositedEvent(
            Long accountId,
            BigDecimal amount) {

        super(
                EventType.MONEY_DEPOSITED,
                "accounts-service"
        );

        this.accountId = accountId;
        this.amount = amount;
    }

    // Used by Jackson to RECREATE the event from Kafka JSON
    @JsonCreator
    public MoneyDepositedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("eventType") EventType eventType,
            @JsonProperty("source") String source,
            @JsonProperty("accountId") Long accountId,
            @JsonProperty("amount") BigDecimal amount) {

        super(
                eventId,
                timestamp,
                eventType,
                source
        );

        this.accountId = accountId;
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}