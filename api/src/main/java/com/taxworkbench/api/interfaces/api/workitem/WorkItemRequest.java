package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.application.workitem.CreateWorkItemCommand;
import com.taxworkbench.api.application.workitem.UpdateWorkItemCommand;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import com.taxworkbench.api.domain.workitem.WorkItemType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class WorkItemRequest {

    public record Create(
            @NotNull(message = "clientId는 필수입니다.")
            @Positive(message = "clientId는 1 이상이어야 합니다.")
            Long clientId,

            @NotNull(message = "type은 필수입니다.")
            WorkItemType type,

            @Size(max = 100, message = "assignee는 100자를 초과할 수 없습니다.")
            String assignee,

            LocalDate dueDate,

            @Size(max = 2000, message = "memo는 2000자를 초과할 수 없습니다.")
            String memo,

            List<@Size(max = 50, message = "각 태그는 50자를 초과할 수 없습니다.") String> tags
    ) {
        public CreateWorkItemCommand toCommand() {
            return new CreateWorkItemCommand(
                    clientId,
                    type,
                    assignee,
                    dueDate,
                    memo,
                    tags == null ? List.of() : tags
            );
        }
    }

    public record Update(
            WorkItemStatus status,

            @Size(max = 100, message = "assignee는 100자를 초과할 수 없습니다.")
            String assignee,

            LocalDate dueDate,

            @Size(max = 2000, message = "memo는 2000자를 초과할 수 없습니다.")
            String memo,

            List<@Size(max = 50, message = "각 태그는 50자를 초과할 수 없습니다.") String> tags,

            @NotNull(message = "expectedVersion은 필수입니다.")
            @PositiveOrZero(message = "expectedVersion은 0 이상이어야 합니다.")
            Long expectedVersion
    ) {
        public UpdateWorkItemCommand toCommand(Long workItemId) {
            return new UpdateWorkItemCommand(
                    workItemId,
                    status,
                    assignee,
                    dueDate,
                    memo,
                    tags,
                    expectedVersion
            );
        }
    }

    public record BulkCreate(
            @NotEmpty(message = "items는 비어 있을 수 없습니다.")
            List<@Valid Create> items
    ) {
        public List<CreateWorkItemCommand> toCommands() {
            return items.stream()
                    .map(Create::toCommand)
                    .toList();
        }
    }
}
