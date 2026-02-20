package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.adapter;

import com.nikitamorozov.docregistry.application.port.out.DocumentStatusHistoryRepositoryPort;
import com.nikitamorozov.docregistry.domain.model.Document;
import com.nikitamorozov.docregistry.domain.model.DocumentStatusHistoryEntry;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentStatusHistoryEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.mapper.DocumentEntityMapper;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository.DocumentStatusHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentStatusHistoryRepositoryAdapter implements DocumentStatusHistoryRepositoryPort {

    private final DocumentStatusHistoryJpaRepository repository;
    private final DocumentEntityMapper documentMapper;

    @Override
    public DocumentStatusHistoryEntry save(DocumentStatusHistoryEntry history) {
        DocumentStatusHistoryEntity entity = new DocumentStatusHistoryEntity();
        DocumentEntity docRef = new DocumentEntity();
        docRef.setId(history.getDocumentId());
        entity.setDocument(docRef);
        entity.setAction(history.getAction());
        entity.setOldStatus(history.getOldStatus());
        entity.setNewStatus(history.getNewStatus());
        entity.setActor(history.getActor());
        entity.setComment(history.getComment());
        entity.setCreatedAt(history.getCreatedAt());

        DocumentStatusHistoryEntity saved = repository.save(entity);
        return new DocumentStatusHistoryEntry(
                saved.getDocument().getId(),
                saved.getAction(),
                saved.getOldStatus(),
                saved.getNewStatus(),
                saved.getActor(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<DocumentStatusHistoryEntry> findByDocumentOrderByCreatedAtAsc(Document document) {
        DocumentEntity jpaEntity = documentMapper.toEntity(document);
        return repository.findByDocumentOrderByCreatedAtAsc(jpaEntity).stream()
                .map(saved -> new DocumentStatusHistoryEntry(
                        saved.getDocument().getId(),
                        saved.getAction(),
                        saved.getOldStatus(),
                        saved.getNewStatus(),
                        saved.getActor(),
                        saved.getComment(),
                        saved.getCreatedAt()
                ))
                .toList();
    }
}
