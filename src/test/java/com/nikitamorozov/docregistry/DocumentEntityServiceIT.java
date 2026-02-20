package com.nikitamorozov.docregistry;

import com.nikitamorozov.docregistry.api.dto.BatchApproveRequest;
import com.nikitamorozov.docregistry.api.dto.BatchOperationResponse;
import com.nikitamorozov.docregistry.api.dto.BatchResultCode;
import com.nikitamorozov.docregistry.api.dto.BatchSubmitRequest;
import com.nikitamorozov.docregistry.api.dto.CreateDocumentRequest;
import com.nikitamorozov.docregistry.api.dto.DocumentSummaryResponse;
import com.nikitamorozov.docregistry.api.dto.DocumentWithHistoryResponse;
import com.nikitamorozov.docregistry.application.port.in.ApproveDocumentsUseCase;
import com.nikitamorozov.docregistry.application.port.in.CreateDocumentUseCase;
import com.nikitamorozov.docregistry.application.port.in.GetDocumentQuery;
import com.nikitamorozov.docregistry.application.port.in.SubmitDocumentsUseCase;
import com.nikitamorozov.docregistry.domain.DocumentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentEntityServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateDocumentUseCase createDocumentUseCase;

    @Autowired
    private SubmitDocumentsUseCase submitDocumentsUseCase;

    @Autowired
    private ApproveDocumentsUseCase approveDocumentsUseCase;

    @Autowired
    private GetDocumentQuery getDocumentQuery;

    @Test
    void happyPath_singleDocument() throws Exception {
        CreateDocumentRequest req = new CreateDocumentRequest("tester", "Happy path");
        String json = objectMapper.writeValueAsString(req);

        String respJson = mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DocumentSummaryResponse created = objectMapper.readValue(respJson, DocumentSummaryResponse.class);
        assertThat(created.status()).isEqualTo(DocumentStatus.DRAFT);

        BatchSubmitRequest submitReq = new BatchSubmitRequest(List.of(created.id()), "tester", "submit");
        BatchOperationResponse submitResp = submitDocumentsUseCase.submitBatch(submitReq.ids(), submitReq.actor(), submitReq.comment());
        assertThat(submitResp.results()).hasSize(1);
        assertThat(submitResp.results().get(0).code()).isEqualTo(BatchResultCode.SUCCESS);

        BatchApproveRequest approveReq = new BatchApproveRequest(List.of(created.id()), "approver", "approve");
        BatchOperationResponse approveResp = approveDocumentsUseCase.approveBatch(approveReq.ids(), approveReq.actor(), approveReq.comment());
        assertThat(approveResp.results()).hasSize(1);
        assertThat(approveResp.results().get(0).code()).isEqualTo(BatchResultCode.SUCCESS);

        DocumentWithHistoryResponse withHistory = getDocumentQuery.getDocumentWithHistory(created.id());
        assertThat(withHistory.document().status()).isEqualTo(DocumentStatus.APPROVED);
        assertThat(withHistory.history()).hasSize(2);
    }

    @Test
    void batchSubmit_partialConflicts() {
        DocumentSummaryResponse d1 = createDocumentUseCase.createDocument("a1", "doc1");
        DocumentSummaryResponse d2 = createDocumentUseCase.createDocument("a2", "doc2");

        submitDocumentsUseCase.submitBatch(List.of(d2.id()), "tester", "pre-submit");

        BatchSubmitRequest submitReq = new BatchSubmitRequest(List.of(d1.id(), d2.id(), 99999L), "tester", "batch");
        BatchOperationResponse resp = submitDocumentsUseCase.submitBatch(submitReq.ids(), submitReq.actor(), submitReq.comment());

        assertThat(resp.results()).hasSize(3);
        assertThat(resp.results().stream().filter(r -> r.code() == BatchResultCode.SUCCESS)).hasSize(1);
        assertThat(resp.results().stream().filter(r -> r.code() == BatchResultCode.CONFLICT)).hasSize(1);
        assertThat(resp.results().stream().filter(r -> r.code() == BatchResultCode.NOT_FOUND)).hasSize(1);
    }

    @Test
    void batchApprove_partialResults() {
        DocumentSummaryResponse d1 = createDocumentUseCase.createDocument("a1", "doc1");
        DocumentSummaryResponse d2 = createDocumentUseCase.createDocument("a2", "doc2");

        submitDocumentsUseCase.submitBatch(List.of(d1.id(), d2.id()), "tester", "submit");

        approveDocumentsUseCase.approveBatch(List.of(d1.id()), "approver", "approve one");

        BatchApproveRequest req = new BatchApproveRequest(List.of(d1.id(), d2.id(), 99999L), "approver", "batch");
        BatchOperationResponse resp = approveDocumentsUseCase.approveBatch(req.ids(), req.actor(), req.comment());

        assertThat(resp.results()).hasSize(3);
        assertThat(resp.results().stream().anyMatch(r -> r.code() == BatchResultCode.SUCCESS)).isTrue();
        assertThat(resp.results().stream().anyMatch(r -> r.code() == BatchResultCode.CONFLICT)).isTrue();
        assertThat(resp.results().stream().anyMatch(r -> r.code() == BatchResultCode.NOT_FOUND)).isTrue();
    }
}

