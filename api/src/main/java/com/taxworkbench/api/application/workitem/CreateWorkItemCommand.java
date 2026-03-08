package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemType;
import java.time.LocalDate;
import java.util.List;

public record CreateWorkItemCommand(
        Long clientId,
        WorkItemType type,
        String assignee,
        LocalDate dueDate,
        String memo,
        List<String> tags
) {}