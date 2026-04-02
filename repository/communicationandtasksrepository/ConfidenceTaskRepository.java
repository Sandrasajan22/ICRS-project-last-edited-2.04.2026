package com.main.icrsbackend.repository.communicationandtasksrepository;

import com.main.icrsbackend.model.communicationandtasks.ConfidenceTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConfidenceTaskRepository extends JpaRepository<ConfidenceTask, Long> {

    List<ConfidenceTask> findByLevelAndSetNumber(String level, Integer setNumber);

    @Query("SELECT t FROM ConfidenceTask t WHERE t.level = :level AND t.setNumber = :setNumber")
    List<ConfidenceTask> findLimitedTasks(String level, Integer setNumber, Pageable pageable);
}