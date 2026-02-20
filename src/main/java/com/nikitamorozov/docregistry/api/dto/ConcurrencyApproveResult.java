package com.nikitamorozov.docregistry.api.dto;

import com.nikitamorozov.docregistry.domain.DocumentStatus;

import java.util.Map;

public record ConcurrencyApproveResult(
        int successCount,
        int conflictOrErrorCount,
        DocumentStatus finalStatus,
        Map<String, Long> errorStats
) {
}

