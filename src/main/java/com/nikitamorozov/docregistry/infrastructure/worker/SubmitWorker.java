package com.nikitamorozov.docregistry.infrastructure.worker;

import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;
import com.nikitamorozov.docregistry.application.port.in.SubmitDocumentsUseCase;
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
public class SubmitWorker {

    private final DocumentJpaRepository documentJpaRepository;
    private final SubmitDocumentsUseCase submitDocumentsUseCase;

    @Value("${workers.submit.enabled:true}")
    private boolean enabled;

    @Value("${workers.submit.batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${workers.submit.interval-ms:10000}")
    @Transactional
    public void run() {
        if (!enabled) {
            return;
        }
        List<Long> ids = documentJpaRepository.findTop1000ByStatusOrderByIdAsc(DocumentStatus.DRAFT).stream()
                .limit(batchSize)
                .map(DocumentEntity::getId)
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        log.info("SubmitWorker: submitting {} documents", ids.size());
        BatchOperationResponse response = submitDocumentsUseCase.submitBatch(ids, "SUBMIT-WORKER", "background submit");
        long success = response.results().stream().filter(r -> r.code().name().equals("SUCCESS")).count();
        log.info("SubmitWorker: success={} total={}", success, ids.size());
    }
}
