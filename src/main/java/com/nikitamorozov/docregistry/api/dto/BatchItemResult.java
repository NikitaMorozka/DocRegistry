package com.nikitamorozov.docregistry.api.dto;

public record BatchItemResult(
        Long id,
        BatchResultCode code,
        String message
) {
}

