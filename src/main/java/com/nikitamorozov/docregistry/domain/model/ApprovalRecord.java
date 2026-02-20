package com.nikitamorozov.docregistry.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;


@RequiredArgsConstructor
@Getter
public class ApprovalRecord {

    private final Long documentId;
    private final String approvedBy;
    private final OffsetDateTime approvedAt;
}

