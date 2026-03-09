package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.application.workitem.*;
import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.audit.AuditRepository;
import com.taxworkbench.api.domain.workitem.WorkItemPage;
import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import com.taxworkbench.api.interfaces.api.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/work-items")
@RequiredArgsConstructor
public class WorkItemController {

    private final CreateWorkItemUseCase createWorkItemUseCase;
    private final UpdateWorkItemUseCase updateWorkItemUseCase;
    private final QueryWorkItemUseCase queryWorkItemUseCase;
    private final BulkCreateWorkItemUseCase bulkCreateWorkItemUseCase;
    private final ExportWorkItemUseCase exportWorkItemUseCase;
    private final AuditRepository auditRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> findAll(
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) WorkItemStatus status,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String dueDateFrom,
            @RequestParam(required = false) String dueDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        WorkItemQuery query = new WorkItemQuery(
                clientName,
                status,
                assignee,
                dueDateFrom != null ? LocalDate.parse(dueDateFrom) : null,
                dueDateTo != null ? LocalDate.parse(dueDateTo) : null,
                page,
                size,
                sortBy,
                sortDir
        );

        WorkItemPage result = queryWorkItemUseCase.execute(query);

        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "content", result.content().stream().map(WorkItemResponse::from).toList(),
                "totalElements", result.totalElements(),
                "totalPages", result.totalPages(),
                "currentPage", result.currentPage(),
                "size", result.size()
        )));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkItemResponse>> create(
            @RequestBody WorkItemRequest.Create request) {
        return ResponseEntity.ok(ApiResponse.ok(
                WorkItemResponse.from(createWorkItemUseCase.execute(request.toCommand()))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItemResponse>> update(
            @PathVariable Long id,
            @RequestBody WorkItemRequest.Update request) {
        return ResponseEntity.ok(ApiResponse.ok(
                WorkItemResponse.from(updateWorkItemUseCase.execute(request.toCommand(id)))));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> bulkCreate(
            @RequestBody WorkItemRequest.BulkCreate request) {
        List<CreateWorkItemCommand> commands = request.items().stream()
                .map(WorkItemRequest.Create::toCommand)
                .toList();
        int count = bulkCreateWorkItemUseCase.execute(commands);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("savedCount", count)));
    }

    @GetMapping("/export")
    public StreamingResponseBody export(
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) WorkItemStatus status,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String dueDateFrom,
            @RequestParam(required = false) String dueDateTo,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletResponse response
    ) {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=workitems.csv");

        WorkItemQuery query = new WorkItemQuery(
                clientName,
                status,
                assignee,
                dueDateFrom != null ? LocalDate.parse(dueDateFrom) : null,
                dueDateTo != null ? LocalDate.parse(dueDateTo) : null,
                0,
                Integer.MAX_VALUE,
                sortBy,
                sortDir
        );

        return outputStream -> exportWorkItemUseCase.execute(query, outputStream);
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(@PathVariable Long id) {
        List<AuditLog> logs = auditRepository.findByEntityTypeAndEntityId("WorkItem", id);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }
}