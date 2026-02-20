package com.nikitamorozov.docregistry.application.port.in;

import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;

import java.util.List;

public interface SubmitDocumentsUseCase {

    BatchOperationResponse submitBatch(List<Long> ids, String actor, String comment);
}

