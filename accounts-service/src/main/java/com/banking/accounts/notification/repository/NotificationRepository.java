package com.banking.accounts.notification.repository;

import com.banking.accounts.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {
	
	
}
