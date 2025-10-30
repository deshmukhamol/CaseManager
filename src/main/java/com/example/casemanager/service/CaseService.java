package com.example.casemanager.service;

import com.example.casemanager.dto.CaseActionRequest;
import com.example.casemanager.dto.CaseCreationRequest;
import com.example.casemanager.dto.CaseDetailResponse;
import com.example.casemanager.dto.CaseDocumentResponse;
import com.example.casemanager.dto.CaseSummaryResponse;
import com.example.casemanager.entity.CaseDocument;
import com.example.casemanager.entity.CaseEntity;
import com.example.casemanager.repository.CaseDocumentRepository;
import com.example.casemanager.repository.CaseRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.CmmnRuntimeService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CaseService {

    private static final String CASE_DEFINITION_KEY = "caseManager";
    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_AWAITING_APPROVAL = "AWAITING_APPROVAL";

    private final CaseRepository caseRepository;
    private final CaseDocumentRepository documentRepository;
    private final CmmnRuntimeService cmmnRuntimeService;
    private final TaskService taskService;
    private final FileStorageService fileStorageService;

    public CaseService(CaseRepository caseRepository,
                       CaseDocumentRepository documentRepository,
                       CmmnRuntimeService cmmnRuntimeService,
                       TaskService taskService,
                       FileStorageService fileStorageService) {
        this.caseRepository = caseRepository;
        this.documentRepository = documentRepository;
        this.cmmnRuntimeService = cmmnRuntimeService;
        this.taskService = taskService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public CaseDetailResponse createCase(CaseCreationRequest request) {
        Instant now = Instant.now();
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey(CASE_DEFINITION_KEY)
            .variable("title", request.getTitle())
            .variable("description", request.getDescription())
            .variable("assignee", request.getAssignee())
            .variable("status", STATUS_OPEN)
            .variable("approver", "manager")
            .start();

        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setCaseInstanceId(caseInstance.getId());
        caseEntity.setTitle(request.getTitle());
        caseEntity.setDescription(request.getDescription());
        caseEntity.setAssignee(request.getAssignee());
        caseEntity.setStatus(STATUS_OPEN);
        caseEntity.setCreatedAt(now);
        caseEntity.setUpdatedAt(now);
        caseRepository.save(caseEntity);

        return toDetailResponse(caseEntity, List.of());
    }

    @Transactional
    public List<CaseSummaryResponse> getCasesForAssignee(String assignee) {
        return caseRepository.findByAssigneeOrderByUpdatedAtDesc(assignee).stream()
            .map(this::toSummaryResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public CaseDetailResponse getCase(Long id) {
        CaseEntity caseEntity = caseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Case not found: " + id));
        List<CaseDocument> documents = documentRepository.findByCaseEntityOrderByUploadedAtDesc(caseEntity);
        return toDetailResponse(caseEntity, documents);
    }

    @Transactional
    public CaseDocumentResponse uploadDocument(Long caseId, MultipartFile file) throws IOException {
        CaseEntity caseEntity = caseRepository.findById(caseId)
            .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        String storagePath = fileStorageService.store(caseEntity.getId(), file);
        CaseDocument document = new CaseDocument();
        document.setCaseEntity(caseEntity);
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setStoragePath(storagePath);
        document.setUploadedAt(Instant.now());

        CaseDocument saved = documentRepository.save(document);
        caseEntity.setUpdatedAt(Instant.now());
        caseRepository.save(caseEntity);
        return toDocumentResponse(saved);
    }

    @Transactional
    public List<CaseDocumentResponse> listDocuments(Long caseId) {
        CaseEntity caseEntity = caseRepository.findById(caseId)
            .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));
        return documentRepository.findByCaseEntityOrderByUploadedAtDesc(caseEntity).stream()
            .map(this::toDocumentResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void submitAction(Long caseId, CaseActionRequest request) {
        CaseEntity caseEntity = caseRepository.findById(caseId)
            .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        Optional<Task> activeTask = findActiveTask(caseEntity.getCaseInstanceId(), caseEntity.getAssignee());
        Map<String, Object> variables = new HashMap<>();
        variables.put("lastAction", request.getAction());
        variables.put("comments", request.getComments());
        variables.put("status", STATUS_AWAITING_APPROVAL);

        activeTask.ifPresent(task -> taskService.complete(task.getId(), variables));
        variables.forEach((key, value) -> cmmnRuntimeService.setVariable(caseEntity.getCaseInstanceId(), key, value));

        caseEntity.setStatus(STATUS_AWAITING_APPROVAL);
        caseEntity.setUpdatedAt(Instant.now());
        caseRepository.save(caseEntity);
    }

    private Optional<Task> findActiveTask(String caseInstanceId, String assignee) {
        TaskQuery query = taskService.createTaskQuery()
            .caseInstanceId(caseInstanceId)
            .taskAssignee(assignee)
            .active();
        return Optional.ofNullable(query.singleResult());
    }

    private CaseSummaryResponse toSummaryResponse(CaseEntity caseEntity) {
        CaseSummaryResponse response = new CaseSummaryResponse();
        response.setId(caseEntity.getId());
        response.setCaseInstanceId(caseEntity.getCaseInstanceId());
        response.setTitle(caseEntity.getTitle());
        response.setDescription(caseEntity.getDescription());
        response.setStatus(caseEntity.getStatus());
        response.setUpdatedAt(caseEntity.getUpdatedAt());
        return response;
    }

    private CaseDetailResponse toDetailResponse(CaseEntity caseEntity, List<CaseDocument> documents) {
        CaseDetailResponse response = new CaseDetailResponse();
        response.setId(caseEntity.getId());
        response.setCaseInstanceId(caseEntity.getCaseInstanceId());
        response.setTitle(caseEntity.getTitle());
        response.setDescription(caseEntity.getDescription());
        response.setStatus(caseEntity.getStatus());
        response.setAssignee(caseEntity.getAssignee());
        response.setCreatedAt(caseEntity.getCreatedAt());
        response.setUpdatedAt(caseEntity.getUpdatedAt());
        response.setDocuments(documents.stream().map(this::toDocumentResponse).collect(Collectors.toList()));
        return response;
    }

    private CaseDocumentResponse toDocumentResponse(CaseDocument document) {
        CaseDocumentResponse response = new CaseDocumentResponse();
        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setContentType(document.getContentType());
        response.setUploadedAt(document.getUploadedAt());
        return response;
    }
}
