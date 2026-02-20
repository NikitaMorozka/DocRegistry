package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "approval_registry")
@Getter
@Setter
public class ApprovalRegistryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;   // <-- нужно именно такое имя

    @Column(name = "approved_by", nullable = false, length = 255)
    private String approvedBy;

    @Column(name = "approved_at", nullable = false)
    private OffsetDateTime approvedAt;
}
