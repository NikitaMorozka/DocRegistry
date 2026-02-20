package com.nikitamorozov.docregistry.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDocumentRequest(
        @NotBlank @Size(max = 255) String author,
        @NotBlank @Size(max = 512) String title
) {
}

