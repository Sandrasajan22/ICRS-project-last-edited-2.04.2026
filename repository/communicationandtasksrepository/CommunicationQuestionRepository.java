package com.main.icrsbackend.repository.communicationandtasksrepository;

import com.main.icrsbackend.model.communicationandtasks.CommunicationQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunicationQuestionRepository extends JpaRepository<CommunicationQuestion, Long> {

    List<CommunicationQuestion> findByStreamAndLevelAndSetNumber(
            String stream,
            String level,
            Integer setNumber
    );

    @Query("SELECT q FROM CommunicationQuestion q WHERE q.stream = :stream AND q.level = :level AND q.setNumber = :setNumber")
    List<CommunicationQuestion> findLimitedQuestions(
            String stream,
            String level,
            Integer setNumber,
            Pageable pageable
    );
}