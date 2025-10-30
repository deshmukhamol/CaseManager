package com.example.casemanager.repository;

import com.example.casemanager.entity.CaseEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {

    List<CaseEntity> findByAssigneeOrderByUpdatedAtDesc(String assignee);

    Optional<CaseEntity> findByCaseInstanceId(String caseInstanceId);
}
