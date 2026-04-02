package com.main.icrsbackend.repository.trainer;

import com.main.icrsbackend.model.trainer.Coursepost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Coursepostrepo extends JpaRepository<Coursepost, Long> {
    List<Coursepost> findByUser_Id(Long userId);
}
