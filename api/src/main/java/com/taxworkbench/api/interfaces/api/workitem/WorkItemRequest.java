package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.application.workitem.CreateWorkItemCommand;
import com.taxworkbench.api.application.workitem.UpdateWorkItemCommand;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import com.taxworkbench.api.domain.workitem.WorkItemType;

import java.time.LocalDate;
import java.util.List;

public class WorkItemRequest {

    public record Create(
            Long clientId,
            WorkItemType type,
            String assignee,
            LocalDate dueDate,
            String memo,
            List<String> tags
    ) {
        public CreateWorkItemCommand toCommand() {
            return new CreateWorkItemCommand(clientId, type, assignee, dueDate, memo, tags);
        }
    }

    public record Update(
            WorkItemStatus status,
            String assignee,
            LocalDate dueDate,
            String memo,
            List<String> tags,
            long expectedVersion
    ) {
        public UpdateWorkItemCommand toCommand(Long workItemId) {
            return new UpdateWorkItemCommand(
                    workItemId, status, assignee, dueDate, memo, tags, expectedVersion);
        }
    }

    public record BulkCreate(List<Create> items) {}
}