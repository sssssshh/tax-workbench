package com.taxworkbench.api.interfaces.api.common;

import com.taxworkbench.api.domain.workitem.WorkItemConflictException;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import com.taxworkbench.api.interfaces.api.workitem.WorkItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final WorkItemRepository workItemRepository;

    @ExceptionHandler(WorkItemConflictException.class)
    public ResponseEntity<?> handleConflict(WorkItemConflictException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "OPTIMISTIC_LOCK_CONFLICT");
        body.put("message", e.getMessage());
        body.put("currentVersion", e.getCurrentVersion());
        body.put("expectedVersion", e.getExpectedVersion());

        // 최신 데이터 포함
        workItemRepository.findById(e.getWorkItemId()).ifPresent(item ->
                body.put("currentData", WorkItemResponse.from(item))
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
    }
}