package com.nikitamorozov.docregistry.application.service;

import com.nikitamorozov.docregistry.api.dto.BatchItemResult;
import com.nikitamorozov.docregistry.api.dto.BatchResultCode;
import com.nikitamorozov.docregistry.api.dto.ConcurrencyApproveRequest;
import com.nikitamorozov.docregistry.api.dto.ConcurrencyApproveResult;
import com.nikitamorozov.docregistry.application.port.in.ConcurrentApproveUseCase;
import com.nikitamorozov.docregistry.application.port.out.DocumentRepositoryPort;
import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.nikitamorozov.docregistry.api.error.ConflictException;
import com.nikitamorozov.docregistry.application.service.ApproveDocumentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConcurrentApproveService implements ConcurrentApproveUseCase {

    private final ApproveDocumentsService approveDocumentsService;
    private final DocumentRepositoryPort documentRepository;

    @Override
    @Transactional
    public ConcurrencyApproveResult testConcurrentApprove(Long id, ConcurrencyApproveRequest req) {
        int threads = req.threads();
        int attempts = req.attempts();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        Map<String, Long> errors = new ConcurrentHashMap<>();
        int[] successCount = {0};

        for (int i = 0; i < attempts; i++) {
            executor.submit(() -> {
                try {
                    BatchItemResult result = approveDocumentsService.approveSingle(id, req.actor(), "concurrency-test");
                    if (result.code() == BatchResultCode.SUCCESS) {
                        synchronized (successCount) {
                            successCount[0]++;
                        }
                    } else {
                        errors.merge(result.code().name(), 1L, Long::sum);
                    }
                } catch (OptimisticLockingFailureException | ConflictException e) {
                    errors.merge("CONFLICT", 1L, Long::sum);
                } catch (Exception e) {
                    errors.merge("ERROR", 1L, Long::sum);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        DocumentStatus finalStatus = documentRepository.findById(id)
                .map(com.nikitamorozov.docregistry.domain.model.Document::getStatus)
                .orElse(null);

        long conflictOrError = errors.entrySet().stream()
                .filter(e -> !Objects.equals(e.getKey(), BatchResultCode.SUCCESS.name()))
                .mapToLong(Map.Entry::getValue)
                .sum();

        return new ConcurrencyApproveResult(
                successCount[0],
                (int) conflictOrError,
                finalStatus,
                errors
        );
    }
}
