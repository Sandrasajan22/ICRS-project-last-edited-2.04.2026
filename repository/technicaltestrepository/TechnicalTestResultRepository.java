package com.main.icrsbackend.repository.technicaltestrepository;

import com.main.icrsbackend.model.technicaltest.TechnicalTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicalTestResultRepository extends JpaRepository<TechnicalTestResult, Long> {

    boolean existsByUserIdAndSkillAndLevelAndFinalEvaluationIn(
            Long userId,
            String skill,
            String level,
            List<String> evaluations
    );
}