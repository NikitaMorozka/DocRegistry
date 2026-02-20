package com.nikitamorozov.docregistry.api;

import com.nikitamorozov.docregistry.api.dto.BatchApproveRequest;
import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;
import com.nikitamorozov.docregistry.api.dto.BatchSubmitRequest;
import com.nikitamorozov.docregistry.api.dto.ConcurrencyApproveRequest;
import com.nikitamorozov.docregistry.api.dto.ConcurrencyApproveResult;
import com.nikitamorozov.docregistry.api.dto.CreateDocumentRequest;
import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.api.dto.DocumentWithHistoryResponse;
import com.nikitamorozov.docregistry.api.dto.PageResponse;
import com.nikitamorozov.docregistry.api.dto.SearchDocumentsRequest;
import com.nikitamorozov.docregistry.application.port.in.ApproveDocumentsUseCase;
import com.nikitamorozov.docregistry.application.port.in.ConcurrentApproveUseCase;
import com.nikitamorozov.docregistry.application.port.in.CreateDocumentUseCase;
import com.nikitamorozov.docregistry.application.port.in.GetDocumentQuery;
import com.nikitamorozov.docregistry.application.port.in.GetDocumentsByIdsQuery;
import com.nikitamorozov.docregistry.application.port.in.SearchDocumentsQuery;
import com.nikitamorozov.docregistry.application.port.in.SubmitDocumentsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents")
@Validated
@RequiredArgsConstructor
public class DocumentController {

    private final CreateDocumentUseCase createDocumentUseCase;
    private final GetDocumentQuery getDocumentQuery;
    private final SearchDocumentsQuery searchDocumentsQuery;
    private final SubmitDocumentsUseCase submitDocumentsUseCase;
    private final ApproveDocumentsUseCase approveDocumentsUseCase;
    private final GetDocumentsByIdsQuery getDocumentsByIdsQuery;
    private final ConcurrentApproveUseCase concurrentApproveUseCase;

    @PostMapping
    @Operation(summary = "Создать документ (статус DRAFT)")
    public DocumentSummaryResponse create(@Valid @RequestBody CreateDocumentRequest request) {
        return createDocumentUseCase.createDocument(request.author(), request.title());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить документ с историей по id")
    public DocumentWithHistoryResponse getById(@PathVariable Long id) {
        return getDocumentQuery.getDocumentWithHistory(id);
    }

    @GetMapping
    @Operation(summary = "Получить список документов (пагинация, сортировка)")
    public PageResponse<DocumentSummaryResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return searchDocumentsQuery.listDocuments(page, size, sortBy, direction);
    }

    @PostMapping("/search")
    @Operation(summary = "Поиск документов по фильтрам (статус, автор, период дат)")
    public PageResponse<DocumentSummaryResponse> search(
            @Valid @RequestBody SearchDocumentsRequest request,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(1000) int size
    ) {
        return searchDocumentsQuery.search(
                request.status(),
                request.author(),
                request.fromDate(),
                request.toDate(),
                page,
                size
        );
    }

    @PostMapping("/submit")
    @Operation(summary = "Пакетно отправить документы на согласование (DRAFT -> SUBMITTED)")
    public BatchOperationResponse submit(@Valid @RequestBody BatchSubmitRequest request) {
        if (request.ids().size() < 1 || request.ids().size() > 1000) {
            throw new IllegalArgumentException("ids size must be between 1 and 1000");
        }
        return submitDocumentsUseCase.submitBatch(request.ids(), request.actor(), request.comment());
    }

    @PostMapping("/approve")
    @Operation(summary = "Пакетно утвердить документы (SUBMITTED -> APPROVED)")
    public BatchOperationResponse approve(@Valid @RequestBody BatchApproveRequest request) {
        if (request.ids().size() < 1 || request.ids().size() > 1000) {
            throw new IllegalArgumentException("ids size must be between 1 and 1000");
        }
        return approveDocumentsUseCase.approveBatch(request.ids(), request.actor(), request.comment());
    }

    @GetMapping("/by-ids")
    @Operation(summary = "Получить список документов по id (без истории)")
    public List<DocumentSummaryResponse> byIds(@RequestParam List<Long> ids) {
        return getDocumentsByIdsQuery.findByIds(ids);
    }

    @PostMapping("/{id}/concurrency-approve-test")
    @Operation(summary = "Тест конкурентного утверждения документа")
    public ConcurrencyApproveResult concurrencyApprove(@PathVariable Long id,
                                                       @Valid @RequestBody ConcurrencyApproveRequest request) {
        return concurrentApproveUseCase.testConcurrentApprove(id, request);
    }
}

