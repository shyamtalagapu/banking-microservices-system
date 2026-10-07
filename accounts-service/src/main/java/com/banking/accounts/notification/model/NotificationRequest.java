package com.banking.accounts.notification.model;

import com.banking.accounts.notification.enums.NotificationChannel;
import com.banking.accounts.notification.enums.NotificationType;

public class NotificationRequest {

    private Long transactionId;
    private Long recipientAccountId;
    private NotificationType type;
    private NotificationChannel channel;
    private String message;

    public NotificationRequest() {
    }

    public NotificationRequest(
            Long transactionId,
            Long recipientAccountId,
            NotificationType type,
            NotificationChannel channel,
            String message) {

        this.transactionId = transactionId;
        this.recipientAccountId = recipientAccountId;
        this.type = type;
        this.channel = channel;
        this.message = message;
    }

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getRecipientAccountId() {
		return recipientAccountId;
	}

	public void setRecipientAccountId(Long recipientAccountId) {
		this.recipientAccountId = recipientAccountId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public NotificationChannel getChannel() {
		return channel;
	}

	public void setChannel(NotificationChannel channel) {
		this.channel = channel;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    // getters and setters
    
    
}
