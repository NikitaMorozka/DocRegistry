package com.nikitamorozov.docregistry.api.error;

public record ApiError(
        String code,
        String message
) {
}

