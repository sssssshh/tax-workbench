package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import java.time.LocalDate;
import java.util.List;

public record UpdateWorkItemCommand(
        Long workItemId,
        WorkItemStatus status,
        String assignee,
        LocalDate dueDate,
        String memo,
        List<String> tags,
        long expectedVersion
) {}