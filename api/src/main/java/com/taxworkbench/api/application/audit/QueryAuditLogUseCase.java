package com.taxworkbench.api.application.audit;

import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryAuditLogUseCase {

    private final AuditRepository auditRepository;

    @Transactional(readOnly = true)
    public List<AuditLog> findWorkItemAuditLogs(Long workItemId) {
        return auditRepository.findByEntityTypeAndEntityId("WorkItem", workItemId);
    }
}
