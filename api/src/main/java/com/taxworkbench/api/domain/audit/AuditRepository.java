package com.taxworkbench.api.domain.audit;

import java.util.List;

public interface AuditRepository {
    void save(AuditLog auditLog);
    void saveAll(List<AuditLog> auditLogs);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}