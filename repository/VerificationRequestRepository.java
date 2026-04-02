package com.main.icrsbackend.repository;

import com.main.icrsbackend.model.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    // ✅ for submit
    Optional<VerificationRequest> findByUser_Id(Long userId);

    // ✅ for admin pending list
    List<VerificationRequest> findByStatus(String status);

    // ✅ for count
    long countByStatus(String status);
}