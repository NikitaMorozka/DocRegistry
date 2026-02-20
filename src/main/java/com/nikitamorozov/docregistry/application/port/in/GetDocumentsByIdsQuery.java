package com.nikitamorozov.docregistry.application.port.in;

import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;

import java.util.List;

public interface GetDocumentsByIdsQuery {

    List<DocumentSummaryResponse> findByIds(List<Long> ids);
}

