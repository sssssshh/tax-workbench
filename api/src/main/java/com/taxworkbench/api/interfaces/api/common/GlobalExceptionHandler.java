package com.taxworkbench.api.interfaces.api.common;

import com.taxworkbench.api.domain.workitem.WorkItemConflictException;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import com.taxworkbench.api.interfaces.api.workitem.WorkItemResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final WorkItemRepository workItemRepository;

    @ExceptionHandler(WorkItemConflictException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleConflict(WorkItemConflictException e) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("currentVersion", e.getCurrentVersion());
        details.put("expectedVersion", e.getExpectedVersion());

        workItemRepository.findById(e.getWorkItemId()).ifPresent(item ->
                details.put("currentData", WorkItemResponse.from(item))
        );

        return error(
                HttpStatus.CONFLICT,
                e.getMessage(),
                ApiError.of("OPTIMISTIC_LOCK_CONFLICT", details)
        );
    }

    @ExceptionHandler({
            ObjectOptimisticLockingFailureException.class,
            OptimisticLockException.class
    })
    public ResponseEntity<ApiResponse<ApiError>> handleJpaOptimisticLock(Exception e) {
        return error(
                HttpStatus.CONFLICT,
                "다른 사용자가 먼저 수정했습니다. 최신 데이터를 확인한 뒤 다시 시도해주세요.",
                ApiError.of("OPTIMISTIC_LOCK_CONFLICT")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleValidation(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return error(
                HttpStatus.BAD_REQUEST,
                "입력값이 올바르지 않습니다.",
                ApiError.of("VALIDATION_ERROR", Map.of("fieldErrors", fieldErrors))
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleIllegalArgument(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage(), ApiError.of("INVALID_ARGUMENT"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleIllegalState(IllegalStateException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage(), ApiError.of("INVALID_STATE"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("parameter", e.getName());
        details.put("value", e.getValue());

        return error(
                HttpStatus.BAD_REQUEST,
                "요청 파라미터 형식이 올바르지 않습니다.",
                ApiError.of("INVALID_PARAMETER", details)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiError>> handleUnexpected(Exception e) {
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다.",
                ApiError.of("INTERNAL_SERVER_ERROR")
        );
    }

    private ResponseEntity<ApiResponse<ApiError>> error(
            HttpStatus status,
            String message,
            ApiError error
    ) {
        return ResponseEntity.status(status).body(ApiResponse.fail(message, error));
    }
}
