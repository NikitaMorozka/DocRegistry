package com.nikitamorozov.docregistry.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BatchSubmitRequest(
        @NotEmpty List<@NotNull Long> ids,
        @NotBlank String actor,
        @Size(max = 1024) String comment
) {
}

