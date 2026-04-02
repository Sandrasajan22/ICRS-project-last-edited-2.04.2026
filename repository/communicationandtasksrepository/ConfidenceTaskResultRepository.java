package com.main.icrsbackend.repository.communicationandtasksrepository;

import com.main.icrsbackend.model.communicationandtasks.ConfidenceTaskResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfidenceTaskResultRepository extends JpaRepository<ConfidenceTaskResult, Long> {

    List<ConfidenceTaskResult> findByUserIdOrderBySubmittedAtDesc(Long userId);

    boolean existsByUserIdAndLevelAndCompletionScoreGreaterThanEqual(
            Long userId,
            String level,
            Integer completionScore
    );
}