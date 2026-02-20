package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.api.dto.DocumentWithHistoryResponse;
import com.nikitamorozov.docregistry.api.dto.StatusHistoryItem;
import com.nikitamorozov.docregistry.application.port.in.GetDocumentQuery;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.application.port.out.DocumentStatusHistoryRepositoryPort;
import com.nikitamorozov.docregistry.api.error.NotFoundException;
import com.nikitamorozov.docregistry.domain.model.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetDocumentService implements GetDocumentQuery {

    private final DocumentRepositoryPort documentRepository;
    private final DocumentStatusHistoryRepositoryPort historyRepository;

    @Override
    @Transactional(readOnly = true)
    public DocumentWithHistoryResponse getDocumentWithHistory(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document " + id + " not found"));
        List<com.nikitamorozov.docregistry.domain.model.DocumentStatusHistoryEntry> history = 
                historyRepository.findByDocumentOrderByCreatedAtAsc(document);
        return new DocumentWithHistoryResponse(
                toSummary(document),
                history.stream()
                        .map(h -> new StatusHistoryItem(
                                h.getAction(),
                                h.getOldStatus(),
                                h.getNewStatus(),
                                h.getActor(),
                                h.getComment(),
                                h.getCreatedAt()
                        ))
                        .toList()
        );
    }

    private DocumentSummaryResponse toSummary(Document d) {
        return new DocumentSummaryResponse(
                d.getId(),
                d.getNumber(),
                d.getAuthor(),
                d.getTitle(),
                d.getStatus(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
}
