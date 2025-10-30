package com.example.casemanager.repository;

import com.example.casemanager.entity.CaseDocument;
import com.example.casemanager.entity.CaseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseDocumentRepository extends JpaRepository<CaseDocument, Long> {

    List<CaseDocument> findByCaseEntityOrderByUploadedAtDesc(CaseEntity caseEntity);
}
