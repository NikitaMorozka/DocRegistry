package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.application.port.in.CreateDocumentUseCase;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.model.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateDocumentService implements CreateDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;

    @Override
    @Transactional
    public DocumentSummaryResponse createDocument(String author, String title) {
        OffsetDateTime now = OffsetDateTime.now();
        Document document = new Document();
        document.setAuthor(author);
        document.setTitle(title);
        document.setStatus(DocumentStatus.DRAFT);
        document.setCreatedAt(now);
        document.setUpdatedAt(now);
        document.setNumber(generateNumber());
        Document saved = documentRepository.save(document);
        log.info("Created document id={}, number={}", saved.getId(), saved.getNumber());
        return toSummary(saved);
    }

    private String generateNumber() {
        return "DOC-" + UUID.randomUUID();
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
