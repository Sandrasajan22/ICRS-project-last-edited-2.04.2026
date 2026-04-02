package com.main.icrsbackend.repository.trainer;

import com.main.icrsbackend.model.trainer.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Notificationsrepo extends JpaRepository<Notifications, Long> {

    List<Notifications> findByTrainer_IdOrderByCreatedAtDesc(Long trainerId);

    List<Notifications> findByTrainer_IdAndReadFalseOrderByCreatedAtDesc(Long trainerId);

    long countByTrainer_IdAndReadFalse(Long trainerId);
}