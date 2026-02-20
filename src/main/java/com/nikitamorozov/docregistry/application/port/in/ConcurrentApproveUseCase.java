package com.nikitamorozov.docregistry.application.port.in;

import com.nikitamorozov.docregistry.api.dto.ConcurrencyApproveRequest;
import com.nikitamorozov.docregistry.api.dto.ConcurrencyApproveResult;

public interface ConcurrentApproveUseCase {

    ConcurrencyApproveResult testConcurrentApprove(Long id, ConcurrencyApproveRequest request);
}

