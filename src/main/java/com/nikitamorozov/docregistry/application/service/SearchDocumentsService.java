package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.api.dto.PageResponse;
import com.nikitamorozov.docregistry.application.port.in.SearchDocumentsQuery;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.model.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SearchDocumentsService implements SearchDocumentsQuery {

    private final DocumentRepositoryPort documentRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentSummaryResponse> listDocuments(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy == null ? "id" : sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> p = documentRepository.findAll(pageable);
        return new PageResponse<>(
                p.map(this::toSummary).getContent(),
                p.getTotalElements(),
                p.getTotalPages(),
                page,
                size
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentSummaryResponse> search(DocumentStatus status,
                                                        String author,
                                                        OffsetDateTime from,
                                                        OffsetDateTime to,
                                                        int page,
                                                        int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Document> p = documentRepository.search(status, author, from, to, pageable);
        return new PageResponse<>(
                p.map(this::toSummary).getContent(),
                p.getTotalElements(),
                p.getTotalPages(),
                page,
                size
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
