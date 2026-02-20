package com.nikitamorozov.docregistry.infrastructure.worker;

import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;
import com.nikitamorozov.docregistry.api.dto.BatchResultCode;
import com.nikitamorozov.docregistry.application.port.in.ApproveDocumentsUseCase;
import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.entity.DocumentEntity;
import com.nikitamorozov.docregistry.infrastructure.persistence.jpa.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApproveWorker {

    private final DocumentJpaRepository documentJpaRepository;
    private final ApproveDocumentsUseCase approveDocumentsUseCase;

    @Value("${workers.approve.enabled:true}")
    private boolean enabled;

    @Value("${workers.approve.batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${workers.approve.interval-ms:10000}")
    @Transactional
    public void run() {
        if (!enabled) {
            return;
        }
        List<Long> ids = documentJpaRepository.findTop1000ByStatusOrderByIdAsc(DocumentStatus.SUBMITTED).stream()
                .limit(batchSize)
                .map(DocumentEntity::getId)
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        log.info("ApproveWorker: approving {} documents", ids.size());
        BatchOperationResponse response = approveDocumentsUseCase.approveBatch(ids, "APPROVE-WORKER", "background approve");
        long success = response.results().stream().filter(r -> r.code() == BatchResultCode.SUCCESS).count();
        long registryErrors = response.results().stream().filter(r -> r.code() == BatchResultCode.REGISTRY_ERROR).count();
        log.info("ApproveWorker: success={} registryErrors={} total={}", success, registryErrors, ids.size());
    }
}
