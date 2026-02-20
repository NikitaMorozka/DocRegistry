package com.nikitamorozov.docregistry.application.port.in;
import com.nikitamorozov.docregistry.api.dto.DocumentWithHistoryResponse;
import org.springframework.stereotype.Component;

@Component
public interface GetDocumentQuery {

    DocumentWithHistoryResponse getDocumentWithHistory(Long id);
}

