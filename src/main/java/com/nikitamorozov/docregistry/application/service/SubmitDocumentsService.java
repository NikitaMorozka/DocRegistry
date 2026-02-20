package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.BatchItemResult;
import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;
import com.nikitamorozov.docregistry.api.dto.BatchResultCode;
import com.nikitamorozov.docregistry.application.port.in.SubmitDocumentsUseCase;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.application.port.out.DocumentStatusHistoryRepositoryPort;
import com.nikitamorozov.docregistry.api.error.NotFoundException;
import com.nikitamorozov.docregistry.domain.StatusAction;
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
public class SubmitDocumentsService implements SubmitDocumentsUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final DocumentStatusHistoryRepositoryPort historyRepository;

    @Override
    @Transactional
    public BatchOperationResponse submitBatch(List<Long> ids, String actor, String comment) {
        List<BatchItemResult> results = new ArrayList<>();
        for (Long id : ids) {
            try {
                Document document = documentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Document " + id + " not found"));
                try {
                    applyStatusChange(document, StatusAction.SUBMIT, actor, comment);
                    results.add(new BatchItemResult(id, BatchResultCode.SUCCESS, "Submitted"));
                } catch (IllegalStateException e) {
                    results.add(new BatchItemResult(id, BatchResultCode.CONFLICT, e.getMessage()));
                }
            } catch (NotFoundException e) {
                results.add(new BatchItemResult(id, BatchResultCode.NOT_FOUND, e.getMessage()));
            }
        }
        return new BatchOperationResponse(results);
    }

    private void applyStatusChange(Document document,
                                  StatusAction action,
                                  String actor,
                                  String comment) {
        OffsetDateTime now = OffsetDateTime.now();
        com.nikitamorozov.docregistry.domain.model.DocumentStatusHistoryEntry entry;
        if (action == StatusAction.SUBMIT) {
            entry = document.submit(actor, comment, now);
        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }
        documentRepository.save(document);
        historyRepository.save(entry);
    }
}
