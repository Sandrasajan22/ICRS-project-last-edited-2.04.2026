package com.main.icrsbackend.repository.academics;

import com.main.icrsbackend.model.jobseeker.Academics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAcademicsRepository extends JpaRepository<Academics, Long> {
    Optional<Academics> findByUserId(Long userId);
}