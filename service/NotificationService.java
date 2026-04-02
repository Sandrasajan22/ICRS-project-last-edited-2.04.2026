package com.main.icrsbackend.service;

import com.main.icrsbackend.model.Notification;
import com.main.icrsbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public void create(Long recipientId, String message, String type, String redirectPath, Long refId) {
        repo.save(new Notification(recipientId, message, type, redirectPath, refId));
    }

    public List<Notification> getForUser(Long userId) {
        return repo.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return repo.countByRecipientIdAndReadStatusFalse(userId);
    }

    public void markAsRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setReadStatus(true);
            repo.save(n);
        });
    }
}

