package com.main.icrsbackend.repository.technicaltestrepository;




import com.main.icrsbackend.model.technicaltest.TechnicalQuestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicalQuestionRepository extends JpaRepository<TechnicalQuestion, Long> {

    List<TechnicalQuestion> findBySkillAndLevelAndSetNumber(
            String skill,
            String level,
            Integer setNumber
    );
}