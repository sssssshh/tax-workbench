package com.taxworkbench.api.interfaces.api.workitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxworkbench.api.application.audit.QueryAuditLogUseCase;
import com.taxworkbench.api.application.workitem.BulkCreateResult;
import com.taxworkbench.api.application.workitem.BulkCreateWorkItemUseCase;
import com.taxworkbench.api.application.workitem.CreateWorkItemUseCase;
import com.taxworkbench.api.application.workitem.ExportWorkItemUseCase;
import com.taxworkbench.api.application.workitem.QueryWorkItemUseCase;
import com.taxworkbench.api.application.workitem.UpdateWorkItemUseCase;
import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;
import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemConflictException;
import com.taxworkbench.api.domain.workitem.WorkItemPage;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import com.taxworkbench.api.domain.workitem.WorkItemType;
import com.taxworkbench.api.interfaces.api.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkItemController.class)
@Import(GlobalExceptionHandler.class)
class WorkItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateWorkItemUseCase createWorkItemUseCase;

    @MockBean
    private UpdateWorkItemUseCase updateWorkItemUseCase;

    @MockBean
    private QueryWorkItemUseCase queryWorkItemUseCase;

    @MockBean
    private BulkCreateWorkItemUseCase bulkCreateWorkItemUseCase;

    @MockBean
    private ExportWorkItemUseCase exportWorkItemUseCase;

    @MockBean
    private QueryAuditLogUseCase queryAuditLogUseCase;

    @MockBean
    private WorkItemRepository workItemRepository;

    @Test
    void findAllReturnsTypedPageEnvelope() throws Exception {
        WorkItem item = workItem(10L, 7L);
        WorkItemPage page = new WorkItemPage(List.of(item), 1, 1, 0, 20);
        given(queryWorkItemUseCase.execute(any())).willReturn(page);

        mockMvc.perform(get("/api/v1/work-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0));
    }

    @Test
    void createReturnsValidationErrorEnvelope() throws Exception {
        String requestJson = """
                {
                  "assignee": "%s"
                }
                """.formatted("a".repeat(101));

        mockMvc.perform(post("/api/v1/work-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.data.details.fieldErrors.clientId").exists())
                .andExpect(jsonPath("$.data.details.fieldErrors.type").exists())
                .andExpect(jsonPath("$.data.details.fieldErrors.assignee").exists());
    }

    @Test
    void bulkCreateReturnsEnvelope() throws Exception {
        given(bulkCreateWorkItemUseCase.execute(any()))
                .willReturn(new BulkCreateResult(2, 1, List.of("row 3: 존재하지 않는 clientId 입니다. (99)")));

        String requestJson = """
                {
                  "items": [
                    { "clientId": 1, "type": "FILING", "assignee": "Kim", "tags": [] },
                    { "clientId": 2, "type": "BOOKKEEPING", "assignee": "Lee", "tags": [] }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/work-items/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.savedCount").value(2))
                .andExpect(jsonPath("$.data.skippedCount").value(1))
                .andExpect(jsonPath("$.data.errors[0]").exists());
    }

    @Test
    void updateReturnsConflictEnvelopeWithCurrentData() throws Exception {
        WorkItem currentItem = workItem(10L, 5L);
        given(updateWorkItemUseCase.execute(any()))
                .willThrow(new WorkItemConflictException(10L, 5L, 4L));
        given(workItemRepository.findById(10L)).willReturn(Optional.of(currentItem));

        String requestJson = """
                {
                  "status": "DONE",
                  "expectedVersion": 4
                }
                """;

        mockMvc.perform(patch("/api/v1/work-items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").value("OPTIMISTIC_LOCK_CONFLICT"))
                .andExpect(jsonPath("$.data.details.currentVersion").value(5))
                .andExpect(jsonPath("$.data.details.expectedVersion").value(4))
                .andExpect(jsonPath("$.data.details.currentData.id").value(10));
    }

    @Test
    void getAuditLogsReturnsTypedResponses() throws Exception {
        AuditLog auditLog = AuditLog.restore(
                1L,
                "WorkItem",
                10L,
                "status",
                "TODO",
                "DONE",
                "system",
                LocalDateTime.of(2026, 3, 10, 10, 0)
        );
        given(queryAuditLogUseCase.findWorkItemAuditLogs(10L)).willReturn(List.of(auditLog));

        mockMvc.perform(get("/api/v1/work-items/10/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].entityType").value("WorkItem"))
                .andExpect(jsonPath("$.data[0].fieldName").value("status"));
    }

    private WorkItem workItem(Long id, Long version) {
        return WorkItem.restore(
                id,
                1L,
                "Acme Tax",
                "123-45-67890",
                WorkItemType.FILING,
                WorkItemStatus.TODO,
                "Kim",
                LocalDate.of(2026, 3, 31),
                List.of("vat"),
                "memo",
                LocalDateTime.of(2026, 3, 10, 9, 0),
                LocalDateTime.of(2026, 3, 10, 9, 30),
                version
        );
    }
}
