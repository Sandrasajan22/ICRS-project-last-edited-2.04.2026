package com.main.icrsbackend.repository.jobseeker;

import com.main.icrsbackend.model.jobseeker.UserCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<UserCertification, Long> {
}