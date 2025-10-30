package com.example.casemanager.dto;

import jakarta.validation.constraints.NotBlank;

public class CaseActionRequest {

    @NotBlank
    private String action;

    private String comments;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
