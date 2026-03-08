package com.taxworkbench.api.domain.workitem;

import java.time.LocalDate;

public record WorkItemQuery(
        String clientName,
        WorkItemStatus status,
        String assignee,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        int page,
        int size,
        String sortBy,
        String sortDir
) {
    public static WorkItemQuery defaultQuery() {
        return new WorkItemQuery(null, null, null, null, null, 0, 20, "createdAt", "desc");
    }
}