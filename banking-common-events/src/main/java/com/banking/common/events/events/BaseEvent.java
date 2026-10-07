package com.banking.common.events.events;

import java.time.LocalDateTime;
import java.util.UUID;

import com.banking.common.events.enums.EventType;


public abstract class BaseEvent {

    private final String eventId;
    private final LocalDateTime timestamp;
    private final EventType eventType;
    private final String source;

    // Producer constructor
    protected BaseEvent(
            EventType eventType,
            String source) {

        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.source = source;
    }

    // Consumer/Jackson constructor
    protected BaseEvent(
            String eventId,
            LocalDateTime timestamp,
            EventType eventType,
            String source) {

        this.eventId = eventId;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.source = source;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getSource() {
        return source;
    }
}