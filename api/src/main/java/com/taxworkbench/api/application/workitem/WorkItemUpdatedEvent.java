package com.taxworkbench.api.application.workitem;

import java.time.LocalDateTime;
import java.util.List;

public record WorkItemUpdatedEvent(
        Long workItemId,
        List<FieldChange> changes,
        String changedBy
) {
    public LocalDateTime occurredAt() {
        return LocalDateTime.now();
    }
}