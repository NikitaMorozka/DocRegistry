package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository;

import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {

    List<DocumentEntity> findTop1000ByStatusOrderByIdAsc(DocumentStatus status);

    @Query("""
        select d from DocumentEntity d
        where (:status is null or d.status = :status)
          and (:author is null or lower(d.author) = lower(:author))
          and (:fromDate is null or d.createdAt >= :fromDate)
          and (:toDate is null or d.createdAt <= :toDate)
        """)
    Page<DocumentEntity> search(
            @Param("status") DocumentStatus status,
            @Param("author") String author,
            @Param("fromDate") OffsetDateTime fromDate,
            @Param("toDate") OffsetDateTime toDate,
            Pageable pageable
    );
}
