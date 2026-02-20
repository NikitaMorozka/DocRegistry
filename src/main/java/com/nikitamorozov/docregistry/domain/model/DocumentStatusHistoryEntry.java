package com.nikitamorozov.docregistry.domain.model;

import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.StatusAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;


@RequiredArgsConstructor
@Getter
public class DocumentStatusHistoryEntry {

    private final Long documentId;
    private final StatusAction action;
    private final DocumentStatus oldStatus;
    private final DocumentStatus newStatus;
    private final String actor;
    private final String comment;
    private final OffsetDateTime createdAt;

}

