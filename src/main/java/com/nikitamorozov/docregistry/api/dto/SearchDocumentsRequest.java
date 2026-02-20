package com.nikitamorozov.docregistry.api.dto;

import com.nikitamorozov.docregistry.domain.DocumentStatus;

import java.time.OffsetDateTime;

public record SearchDocumentsRequest(
        DocumentStatus status,
        String author,
        OffsetDateTime fromDate,
        OffsetDateTime toDate
) {
}

