package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository;

import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentStatusHistoryJpaRepository extends JpaRepository<DocumentStatusHistoryEntity, Long> {

    List<DocumentStatusHistoryEntity> findByDocumentOrderByCreatedAtAsc(DocumentEntity document);
}
