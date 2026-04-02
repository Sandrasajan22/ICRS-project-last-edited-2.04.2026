package com.main.icrsbackend.repository.academics;

import com.main.icrsbackend.model.jobseeker.UserCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {
    List<UserCertification> findByUserIdOrderByIdAsc(Long userId);
    void deleteByUserId(Long userId);
    long countByUserId(Long userId);
}