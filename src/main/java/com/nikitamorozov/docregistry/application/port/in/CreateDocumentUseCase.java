package com.nikitamorozov.docregistry.application.port.in;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public interface CreateDocumentUseCase {

    DocumentSummaryResponse createDocument(String author, String title);
}

