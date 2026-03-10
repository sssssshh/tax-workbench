package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.application.audit.QueryAuditLogUseCase;
import com.taxworkbench.api.application.workitem.*;
import com.taxworkbench.api.interfaces.api.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-items")
@RequiredArgsConstructor
public class WorkItemController {

    private final CreateWorkItemUseCase createWorkItemUseCase;
    private final UpdateWorkItemUseCase updateWorkItemUseCase;
    private final QueryWorkItemUseCase queryWorkItemUseCase;
    private final BulkCreateWorkItemUseCase bulkCreateWorkItemUseCase;
    private final ExportWorkItemUseCase exportWorkItemUseCase;
    private final QueryAuditLogUseCase queryAuditLogUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<WorkItemPageResponse>> findAll(
            @Valid @ModelAttribute WorkItemQueryRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                WorkItemPageResponse.from(queryWorkItemUseCase.execute(request.toQuery()))
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkItemResponse>> create(
            @Valid @RequestBody WorkItemRequest.Create request) {
        return ResponseEntity.ok(ApiResponse.ok(
                WorkItemResponse.from(createWorkItemUseCase.execute(request.toCommand()))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItemResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkItemRequest.Update request) {
        return ResponseEntity.ok(ApiResponse.ok(
                WorkItemResponse.from(updateWorkItemUseCase.execute(request.toCommand(id)))));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<BulkCreateResponse>> bulkCreate(
            @Valid @RequestBody WorkItemRequest.BulkCreate request) {
        return ResponseEntity.ok(ApiResponse.ok(
                BulkCreateResponse.from(bulkCreateWorkItemUseCase.execute(request.toCommands()))
        ));
    }

    @GetMapping("/export")
    public StreamingResponseBody export(
            @ModelAttribute WorkItemQueryRequest request,
            HttpServletResponse response
    ) {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=workitems.csv");

        return outputStream -> exportWorkItemUseCase.execute(request.toExportQuery(), outputStream);
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                queryAuditLogUseCase.findWorkItemAuditLogs(id).stream()
                        .map(AuditLogResponse::from)
                        .toList()
        ));
    }
}
