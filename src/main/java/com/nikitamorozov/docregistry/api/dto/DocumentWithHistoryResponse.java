package com.nikitamorozov.docregistry.api.dto;

import java.util.List;

public record DocumentWithHistoryResponse(
        DocumentSummaryResponse document,
        List<StatusHistoryItem> history
) {
}

