package com.banking.accounts.notification.service;

import com.banking.accounts.notification.entity.Notification;
import com.banking.accounts.notification.enums.NotificationStatus;
import com.banking.accounts.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AsyncNotificationService {

    private final NotificationRepository notificationRepository;

    public AsyncNotificationService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async("notificationExecutor")
    public void processNotification(Notification notification) {

        System.out.println(
                "Started notification processing | "
                        + "notificationId=" + notification.getId()
                        + " | channel=" + notification.getChannel()
                        + " | thread="
                        + Thread.currentThread().getName()
        );

        try {

            // Temporary delay so that we can clearly observe
            // asynchronous execution during testing.
            Thread.sleep(2000);

            switch (notification.getChannel()) {

                case EMAIL:
                    sendEmail(notification);
                    break;

                case SMS:
                    sendSms(notification);
                    break;

                case INAPP:
                    createInAppNotification(notification);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Unsupported notification channel: "
                                    + notification.getChannel()
                    );
            }

            notification.setStatus(
                    NotificationStatus.SUCCESS
            );

            notification.setProcessedAt(LocalDateTime.now());

            notificationRepository.save(notification);

            System.out.println(
                    "Completed notification processing | "
                            + "notificationId=" + notification.getId()
                            + " | channel=" + notification.getChannel()
                            + " | thread="
                            + Thread.currentThread().getName()
            );

        } catch (Exception exception) {

            notification.setStatus(
                    NotificationStatus.FAILED
            );

            notification.setProcessedAt(LocalDateTime.now());

            notificationRepository.save(notification);

            System.err.println(
                    "Notification processing failed | "
                            + "notificationId=" + notification.getId()
                            + " | error="
                            + exception.getMessage()
            );
        }
    }

    private void sendEmail(Notification notification) {

        System.out.println(
                "[EMAIL] Sending: "
                        + notification.getMessage()
        );
    }

    private void sendSms(Notification notification) {

        System.out.println(
                "[SMS] Sending: "
                        + notification.getMessage()
        );
    }

    private void createInAppNotification(
            Notification notification) {

        System.out.println(
                "[IN-APP] Creating: "
                        + notification.getMessage()
        );
    }
}
