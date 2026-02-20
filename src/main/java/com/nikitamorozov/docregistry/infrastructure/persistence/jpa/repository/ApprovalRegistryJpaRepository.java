package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository;

import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.ApprovalRegistryEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprovalRegistryJpaRepository extends JpaRepository<ApprovalRegistryEntity, Long> {
    Optional<ApprovalRegistryEntity> findByDocument(DocumentEntity document);
}
