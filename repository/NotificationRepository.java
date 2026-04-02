package com.main.icrsbackend.repository;

import com.main.icrsbackend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipientIdAndReadStatusFalse(Long recipientId);

    // Keep these for admin/system broadcast if needed, though they could also use recipientId = 0L
    List<Notification> findByRecipientIdAndReadStatusFalseOrderByCreatedAtDesc(Long recipientId);
}
