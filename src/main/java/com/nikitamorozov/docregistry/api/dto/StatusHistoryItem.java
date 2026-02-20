package com.nikitamorozov.docregistry.api.dto;

import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.StatusAction;

import java.time.OffsetDateTime;

public record StatusHistoryItem(
        StatusAction action,
        DocumentStatus oldStatus,
        DocumentStatus newStatus,
        String actor,
        String comment,
        OffsetDateTime createdAt
) {
}

