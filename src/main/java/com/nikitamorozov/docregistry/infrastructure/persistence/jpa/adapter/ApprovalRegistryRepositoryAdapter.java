package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.adapter;

import com.nikitamorozov.docregistry.application.port.out.ApprovalRegistryPort;
import com.nikitamorozov.docregistry.domain.model.ApprovalRecord;
import com.nikitamorozov.docregistry.domain.model.Document;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.ApprovalRegistryEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.mapper.DocumentEntityMapper;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository.ApprovalRegistryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApprovalRegistryRepositoryAdapter implements ApprovalRegistryPort {

    private final ApprovalRegistryJpaRepository repository;
    private final DocumentEntityMapper documentMapper;

    @Override
    public ApprovalRecord save(ApprovalRecord approvalRegistry) {
        ApprovalRegistryEntity entity = new ApprovalRegistryEntity();
        DocumentEntity docRef = new DocumentEntity();
        docRef.setId(approvalRegistry.getDocumentId());
        entity.setDocument(docRef);
        entity.setApprovedBy(approvalRegistry.getApprovedBy());
        entity.setApprovedAt(approvalRegistry.getApprovedAt());

        ApprovalRegistryEntity saved = repository.save(entity);
        return new ApprovalRecord(
                saved.getDocument().getId(),
                saved.getApprovedBy(),
                saved.getApprovedAt()
        );
    }

    @Override
    public Optional<ApprovalRecord> findByDocument(Document document) {
        DocumentEntity jpaEntity = documentMapper.toEntity(document);
        return repository.findByDocument(jpaEntity)
                .map(saved -> new ApprovalRecord(
                        saved.getDocument().getId(),
                        saved.getApprovedBy(),
                        saved.getApprovedAt()
                ));
    }
}
