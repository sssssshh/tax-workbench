package com.taxworkbench.api.infrastructure.audit;

import com.taxworkbench.api.application.workitem.WorkItemUpdatedEvent;
import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditRepository auditRepository;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleWorkItemUpdated(WorkItemUpdatedEvent event) {
        List<AuditLog> logs = event.changes().stream()
                .map(change -> AuditLog.of(
                        "WorkItem",
                        event.workItemId(),
                        change.fieldName(),
                        change.oldValue(),
                        change.newValue(),
                        event.changedBy()
                ))
                .toList();

        auditRepository.saveAll(logs);
    }
}