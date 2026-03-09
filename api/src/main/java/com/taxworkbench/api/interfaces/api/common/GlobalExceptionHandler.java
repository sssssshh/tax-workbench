package com.taxworkbench.api.interfaces.api.common;

import com.taxworkbench.api.domain.workitem.WorkItemConflictException;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import com.taxworkbench.api.interfaces.api.workitem.WorkItemResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

        workItemRepository.findById(e.getWorkItemId()).ifPresent(item ->
                body.put("currentData", WorkItemResponse.from(item))
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({
            ObjectOptimisticLockingFailureException.class,
            OptimisticLockException.class
    })
    public ResponseEntity<?> handleJpaOptimisticLock(Exception e) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "OPTIMISTIC_LOCK_CONFLICT");
        body.put("message", "다른 사용자가 먼저 수정했습니다. 최신 데이터를 확인한 뒤 다시 시도해주세요.");

        // JPA 예외만으로는 항상 workItemId를 정확히 뽑기 어렵기 때문에
        // 프론트는 이 경우 목록 재조회 또는 currentData fallback 없이 처리해도 됩니다.
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