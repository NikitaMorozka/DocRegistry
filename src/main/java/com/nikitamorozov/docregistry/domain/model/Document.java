package com.nikitamorozov.docregistry.domain.model;

import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.StatusAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;


@NoArgsConstructor
@Getter
@Setter
public class Document {

    private Long id;
    private String number;
    private String author;
    private String title;
    private DocumentStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;

    public DocumentStatusHistoryEntry submit(String actor, String comment, OffsetDateTime at) {
        if (status != DocumentStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit document in status " + status);
        }
        DocumentStatus old = this.status;
        this.status = DocumentStatus.SUBMITTED;
        this.updatedAt = at;
        return new DocumentStatusHistoryEntry(
                id,
                StatusAction.SUBMIT,
                old,
                this.status,
                actor,
                comment,
                at
        );
    }

    public DocumentStatusHistoryEntry approve(String actor, String comment, OffsetDateTime at) {
        if (status != DocumentStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot approve document in status " + status);
        }
        DocumentStatus old = this.status;
        this.status = DocumentStatus.APPROVED;
        this.updatedAt = at;
        return new DocumentStatusHistoryEntry(
                id,
                StatusAction.APPROVE,
                old,
                this.status,
                actor,
                comment,
                at
        );
    }
}

