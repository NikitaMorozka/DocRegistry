package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity;

import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.domain.StatusAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "document_status_history")
@Getter
@Setter
public class DocumentStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 32)
    private StatusAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 32)
    private DocumentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 32)
    private DocumentStatus newStatus;

    @Column(name = "actor", nullable = false, length = 255)
    private String actor;

    @Column(name = "comment", length = 1024)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
