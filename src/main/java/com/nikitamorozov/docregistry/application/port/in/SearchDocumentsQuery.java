package com.nikitamorozov.docregistry.application.port.in;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.api.dto.PageResponse;
import com.nikitamorozov.docregistry.domain.DocumentStatus;

import java.time.OffsetDateTime;

public interface SearchDocumentsQuery {

    PageResponse<DocumentSummaryResponse> listDocuments(int page, int size, String sortBy, String direction);

    PageResponse<DocumentSummaryResponse> search(DocumentStatus status,
                                                 String author,
                                                 OffsetDateTime from,
                                                 OffsetDateTime to,
                                                 int page,
                                                 int size);
}

