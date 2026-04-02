package com.main.icrsbackend.repository.trainer;

import com.main.icrsbackend.model.trainer.Trainerprofile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Profilerepo extends JpaRepository<Trainerprofile, Long> {

    // ✅ since Trainerprofile has field: private User user;
    // ✅ this matches user.id
    Optional<Trainerprofile> findByUserId(Long userId);
}