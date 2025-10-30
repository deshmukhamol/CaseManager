package com.example.casemanager.dto;

import java.time.Instant;
import java.util.List;

public class CaseDetailResponse {

    private Long id;
    private String caseInstanceId;
    private String title;
    private String description;
    private String status;
    private String assignee;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CaseDocumentResponse> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseInstanceId() {
        return caseInstanceId;
    }

    public void setCaseInstanceId(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CaseDocumentResponse> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CaseDocumentResponse> documents) {
        this.documents = documents;
    }
}
