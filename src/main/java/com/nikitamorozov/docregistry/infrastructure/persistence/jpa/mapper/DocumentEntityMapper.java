package com.nikitamorozov.docregistry.infrastructure.persistence.jpa.mapper;

import com.nikitamorozov.docregistry.domain.model.Document;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DocumentEntityMapper {

    Document toDomain(DocumentEntity entity);

    DocumentEntity toEntity(Document domain);
}
