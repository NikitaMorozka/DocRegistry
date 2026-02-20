package com.nikitamorozov.docregistry.api.dto;

import com.nikitamorozov.docregistry.domain.DocumentStatus;

import java.time.OffsetDateTime;

public record DocumentSummaryResponse(
        Long id,
        String number,
        String author,
        String title,
        DocumentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

