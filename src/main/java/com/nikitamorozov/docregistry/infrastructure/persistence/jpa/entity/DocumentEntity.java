package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity;

import com.nikitamorozov.docregistry.domain.DocumentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_number", nullable = false, unique = true, length = 64)
    private String number;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "title", nullable = false, length = 512)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DocumentStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    private Long version;
}
