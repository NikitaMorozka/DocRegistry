package com.nikitamorozov.docregistry.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ConcurrencyApproveRequest(
        @NotBlank String actor,
        @Min(1) @Max(64) int threads,
        @Min(1) @Max(1000) int attempts
) {
}

