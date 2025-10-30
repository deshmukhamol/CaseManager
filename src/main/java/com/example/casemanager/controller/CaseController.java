package com.example.casemanager.controller;

import com.example.casemanager.dto.CaseActionRequest;
import com.example.casemanager.dto.CaseCreationRequest;
import com.example.casemanager.dto.CaseDetailResponse;
import com.example.casemanager.dto.CaseDocumentResponse;
import com.example.casemanager.dto.CaseSummaryResponse;
import com.example.casemanager.service.CaseService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<CaseDetailResponse> createCase(@Valid @RequestBody CaseCreationRequest request) {
        return ResponseEntity.ok(caseService.createCase(request));
    }

    @GetMapping("/assigned/{assignee}")
    public ResponseEntity<List<CaseSummaryResponse>> getCasesForAssignee(@PathVariable String assignee) {
        return ResponseEntity.ok(caseService.getCasesForAssignee(assignee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseDetailResponse> getCase(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCase(id));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CaseDocumentResponse> uploadDocument(@PathVariable Long id,
                                                               @RequestParam("file") MultipartFile file)
        throws IOException {
        return ResponseEntity.ok(caseService.uploadDocument(id, file));
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<CaseDocumentResponse>> listDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.listDocuments(id));
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<Void> submitAction(@PathVariable Long id,
                                             @Valid @RequestBody CaseActionRequest request) {
        caseService.submitAction(id, request);
        return ResponseEntity.accepted().build();
    }
}
