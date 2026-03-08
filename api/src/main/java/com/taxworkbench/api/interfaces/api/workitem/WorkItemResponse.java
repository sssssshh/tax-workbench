package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import com.taxworkbench.api.domain.workitem.WorkItemType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record WorkItemResponse(
        Long id,
        Long clientId,
        String clientName,
        String bizNo,
        WorkItemType type,
        WorkItemStatus status,
        String assignee,
        LocalDate dueDate,
        List<String> tags,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long version
) {
    public static WorkItemResponse from(WorkItem item) {
        return new WorkItemResponse(
                item.getId(),
                item.getClientId(),
                item.getClientName(),
                item.getBizNo(),
                item.getType(),
                item.getStatus(),
                item.getAssignee(),
                item.getDueDate(),
                item.getTags(),
                item.getMemo(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getVersion()
        );
    }
}