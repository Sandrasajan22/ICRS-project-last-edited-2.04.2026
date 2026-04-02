package com.main.icrsbackend.repository.profilerepository;

import com.main.icrsbackend.model.profile.InstitutionProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // optional
public interface InstitutionProfileRepository extends JpaRepository<InstitutionProfile, Long> {
    Optional<InstitutionProfile> findByUserId(Long userId);
}
