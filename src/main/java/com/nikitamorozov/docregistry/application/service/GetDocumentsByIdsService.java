package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.application.port.in.GetDocumentsByIdsQuery;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.domain.model.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDocumentsByIdsService implements GetDocumentsByIdsQuery {

    private final DocumentRepositoryPort documentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSummaryResponse> findByIds(List<Long> ids) {
        return documentRepository.findAllById(ids).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
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
