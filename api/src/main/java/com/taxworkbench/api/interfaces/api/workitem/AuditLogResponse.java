package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.domain.audit.AuditLog;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String entityType,
        Long entityId,
        String fieldName,
        String oldValue,
        String newValue,
        String changedBy,
        LocalDateTime changedAt
) {
    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getFieldName(),
                auditLog.getOldValue(),
                auditLog.getNewValue(),
                auditLog.getChangedBy(),
                auditLog.getChangedAt()
        );
    }
}
