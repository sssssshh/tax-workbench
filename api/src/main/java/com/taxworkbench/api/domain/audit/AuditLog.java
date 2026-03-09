package com.taxworkbench.api.domain.audit;

import java.time.LocalDateTime;

public class AuditLog {

    private Long id;
    private String entityType;
    private Long entityId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String changedBy;
    private LocalDateTime changedAt;

    public static AuditLog of(
            String entityType,
            Long entityId,
            String fieldName,
            String oldValue,
            String newValue,
            String changedBy
    ) {
        AuditLog log = new AuditLog();
        log.entityType = entityType;
        log.entityId = entityId;
        log.fieldName = fieldName;
        log.oldValue = oldValue;
        log.newValue = newValue;
        log.changedBy = changedBy;
        log.changedAt = LocalDateTime.now();
        return log;
    }

    public static AuditLog restore(
            Long id,
            String entityType,
            Long entityId,
            String fieldName,
            String oldValue,
            String newValue,
            String changedBy,
            LocalDateTime changedAt
    ) {
        AuditLog log = new AuditLog();
        log.id = id;
        log.entityType = entityType;
        log.entityId = entityId;
        log.fieldName = fieldName;
        log.oldValue = oldValue;
        log.newValue = newValue;
        log.changedBy = changedBy;
        log.changedAt = changedAt;
        return log;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}