package com.main.icrsbackend.repository.trainer;

import com.main.icrsbackend.model.trainer.Trainercourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Courserepo extends JpaRepository<Trainercourse, Long> {

    List<Trainercourse> findByUserIdOrderByCreatedAtDesc(Long userId);
}