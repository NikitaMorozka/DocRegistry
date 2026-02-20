package com.nikitamorozov.docregistry.api.dto;

import java.util.List;

public record BatchOperationResponse(
        List<BatchItemResult> results
) {
}

