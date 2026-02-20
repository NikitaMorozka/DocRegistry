package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.BatchItemResult;
import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;
import com.nikitamorozov.docregistry.api.dto.BatchResultCode;
import com.nikitamorozov.docregistry.application.port.in.ApproveDocumentsUseCase;
import com.nikitamorozov.docregistry.application.port.out.ApprovalRegistryPort;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.application.port.out.DocumentStatusHistoryRepositoryPort;
import com.nikitamorozov.docregistry.api.error.NotFoundException;
import com.nikitamorozov.docregistry.domain.StatusAction;
import com.nikitamorozov.docregistry.domain.model.ApprovalRecord;
import com.nikitamorozov.docregistry.domain.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApproveDocumentsService implements ApproveDocumentsUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final DocumentStatusHistoryRepositoryPort historyRepository;
    private final ApprovalRegistryPort approvalRegistryRepository;

    @Override
    @Transactional
    public BatchOperationResponse approveBatch(List<Long> ids, String actor, String comment) {
        List<BatchItemResult> results = new ArrayList<>();
        for (Long id : ids) {
            try {
                BatchItemResult result = approveSingle(id, actor, comment);
                results.add(result);
            } catch (NotFoundException e) {
                results.add(new BatchItemResult(id, BatchResultCode.NOT_FOUND, e.getMessage()));
            }
        }
        return new BatchOperationResponse(results);
    }

    @Transactional
    public BatchItemResult approveSingle(Long id, String actor, String comment) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document " + id + " not found"));
        try {
            applyStatusChange(document, StatusAction.APPROVE, actor, comment);
            ApprovalRecord registry = new ApprovalRecord(document.getId(), actor, OffsetDateTime.now());
            approvalRegistryRepository.save(registry);
            return new BatchItemResult(id, BatchResultCode.SUCCESS, "Approved");
        } catch (IllegalStateException e) {
            return new BatchItemResult(id, BatchResultCode.CONFLICT, e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private void applyStatusChange(Document document,
                                  StatusAction action,
                                  String actor,
                                  String comment) {
        OffsetDateTime now = OffsetDateTime.now();
        com.nikitamorozov.docregistry.domain.model.DocumentStatusHistoryEntry entry;
        if (action == StatusAction.APPROVE) {
            entry = document.approve(actor, comment, now);
        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }
        documentRepository.save(document);
        historyRepository.save(entry);
    }
}
