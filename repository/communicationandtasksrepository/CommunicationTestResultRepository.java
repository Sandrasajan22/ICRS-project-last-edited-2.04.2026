package com.main.icrsbackend.repository.communicationandtasksrepository;

import com.main.icrsbackend.model.communicationandtasks.CommunicationTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunicationTestResultRepository extends JpaRepository<CommunicationTestResult, Long> {

    List<CommunicationTestResult> findByUserIdOrderBySubmittedAtDesc(Long userId);

    boolean existsByUserIdAndStreamAndLevelAndFinalEvaluationIn(
            Long userId,
            String stream,
            String level,
            List<String> evaluations
    );
}