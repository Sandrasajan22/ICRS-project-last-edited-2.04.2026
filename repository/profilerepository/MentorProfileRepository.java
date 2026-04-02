package com.main.icrsbackend.repository.profilerepository;

import com.main.icrsbackend.model.profile.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // optional, Spring Data will pick it up without this
public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByUserId(Long userId);
}
