package com.banking.accounts.notification.service;

import com.banking.accounts.notification.entity.Notification;

import com.banking.accounts.notification.enums.NotificationChannel;
import com.banking.accounts.notification.enums.NotificationStatus;
import com.banking.accounts.notification.enums.NotificationType;
import com.banking.accounts.notification.model.NotificationRequest;
import com.banking.accounts.notification.repository.NotificationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class NotificationService {

	private final Logger logger= LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final AsyncNotificationService asyncNotificationService;
    

    public NotificationService(NotificationRepository notificationRepository,
			AsyncNotificationService asyncNotificationService) {
		super();
		this.notificationRepository = notificationRepository;
		this.asyncNotificationService = asyncNotificationService;
	}



	private void processChannel(Notification notification) {

        NotificationChannel channel =
                notification.getChannel();

        switch (channel) {

            case EMAIL -> sendEmail(notification);

            case SMS -> sendSms(notification);

            case INAPP -> createInAppNotification(notification);

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported notification channel: "
                                    + channel
                    );
        }
    }

    private void sendEmail(Notification notification) {

        logger.info(
                "[EMAIL] Sending notification for transaction "
                        + notification.getTransactionId()
        );

        logger.info(
                "[EMAIL] " + notification.getMessage()
        );
    }

    private void sendSms(Notification notification) {

    	 logger.info(
                "[SMS] Sending notification for transaction "
                        + notification.getTransactionId()
        );

    	 logger.info(
                "[SMS] " + notification.getMessage()
        );
    }

    private void createInAppNotification(
            Notification notification) {

    	 logger.info(
                "[IN-APP] Creating notification for transaction "
                        + notification.getTransactionId()
        );

    	 logger.info(
                "[IN-APP] " + notification.getMessage()
        );
    }
    
    

    private void createNotification( Long transactionId,
            Long recipientAccountId,
            NotificationType type,
            String message) {

        processNotification(
            new NotificationRequest(
                transactionId,
                recipientAccountId,
                type,
                NotificationChannel.EMAIL,
                message
            )
        );

        processNotification(
            new NotificationRequest(
                transactionId,
                recipientAccountId,
                type,
                NotificationChannel.SMS,
                message
            )
        );

        processNotification(
            new NotificationRequest(
                transactionId,
                recipientAccountId,
                type,
                NotificationChannel.INAPP,
                message
            )
        );
    }
 @Async("notificationExecutor")   
public void processNotification(NotificationRequest request) {
    	

        Notification notification = new Notification();
        
        logger.info("Notification Request : "+request.getType() + " transactionId : "+request.getTransactionId() );

        notification.setTransactionId(request.getTransactionId());
        notification.setRecipientAccountId(
                request.getRecipientAccountId()
        );
        notification.setType(request.getType());
        notification.setChannel(request.getChannel());
        notification.setMessage(request.getMessage());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());

        notification =
                notificationRepository.save(notification);

        try {

            processChannel(notification);

            notification.setStatus(NotificationStatus.SUCCESS);
            notification.setProcessedAt(LocalDateTime.now());

            notificationRepository.save(notification);
            asyncNotificationService.processNotification(notification);

        } catch (Exception exception) {

            notification.setStatus(NotificationStatus.FAILED);
            notification.setProcessedAt(LocalDateTime.now());

            notificationRepository.save(notification);

            System.err.println(
                    "Notification processing failed for ID "
                            + notification.getId()
            );
        }
    }


public void notifyTransferSuccess(
        Long transactionId,
        Long senderAccountId,
        Long receiverAccountId,
        BigDecimal amount) {

    String message =
            "Transfer of ₹" + amount
                    + " completed successfully.";

    createNotification(
            transactionId,
            senderAccountId,
            NotificationType.TRANSACTION_SUCCESS,
            message
    );

    createNotification(
            transactionId,
            receiverAccountId,
            NotificationType.TRANSACTION_SUCCESS,
            message
    );
}
}
