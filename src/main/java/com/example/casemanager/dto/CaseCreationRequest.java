package com.example.casemanager.dto;

import jakarta.validation.constraints.NotBlank;

public class CaseCreationRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String assignee;

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

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
