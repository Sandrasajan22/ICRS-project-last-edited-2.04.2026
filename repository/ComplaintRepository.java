package com.main.icrsbackend.repository;

import com.main.icrsbackend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);
    List<Complaint> findByReporter_Id(Long userId);
}
