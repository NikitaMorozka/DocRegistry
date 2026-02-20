package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.adapter;

import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.model.Document;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.mapper.DocumentEntityMapper;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentJpaRepository documentJpaRepository;
    private final DocumentEntityMapper mapper;

    @Override
    public Optional<Document> findById(Long id) {
        return documentJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Document save(Document document) {
        return mapper.toDomain(documentJpaRepository.save(mapper.toEntity(document)));
    }

    @Override
    public Page<Document> findAll(Pageable pageable) {
        return documentJpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Document> search(DocumentStatus status,
                                 String author,
                                 OffsetDateTime fromDate,
                                 OffsetDateTime toDate,
                                 Pageable pageable) {
        return documentJpaRepository.search(status, author, fromDate, toDate, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<Document> findTop1000ByStatusOrderByIdAsc(DocumentStatus status) {
        return documentJpaRepository.findTop1000ByStatusOrderByIdAsc(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Document> findAllById(Iterable<Long> ids) {
        return documentJpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
