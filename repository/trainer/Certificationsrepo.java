package com.main.icrsbackend.repository.trainer;

import com.main.icrsbackend.model.trainer.Trainercertifications;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Certificationsrepo extends JpaRepository<Trainercertifications, Long> {

    // ✅ used by controller list()
    List<Trainercertifications> findByTrainerIdOrderByCreatedAtDesc(Long trainerId);
}