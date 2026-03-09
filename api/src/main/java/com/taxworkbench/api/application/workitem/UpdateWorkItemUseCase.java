package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UpdateWorkItemUseCase {

    private final WorkItemRepository workItemRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public WorkItem execute(UpdateWorkItemCommand command) {
        WorkItem workItem = workItemRepository.findById(command.workItemId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 업무입니다: " + command.workItemId()));

        // 1차 빠른 검증: 이미 화면 버전이 오래된 경우 즉시 409
        workItem.checkVersion(command.expectedVersion());

        List<FieldChange> changes = new ArrayList<>();

        if (command.status() != null && !command.status().equals(workItem.getStatus())) {
            changes.add(new FieldChange(
                    "status",
                    workItem.getStatus() == null ? null : workItem.getStatus().name(),
                    command.status().name()
            ));
            workItem.changeStatus(command.status());
        }

        if (command.assignee() != null && !command.assignee().equals(workItem.getAssignee())) {
            changes.add(new FieldChange(
                    "assignee",
                    workItem.getAssignee(),
                    command.assignee()
            ));
            workItem.updateAssignee(command.assignee());
        }

        if (command.dueDate() != null && !command.dueDate().equals(workItem.getDueDate())) {
            changes.add(new FieldChange(
                    "dueDate",
                    String.valueOf(workItem.getDueDate()),
                    String.valueOf(command.dueDate())
            ));
            workItem.updateDueDate(command.dueDate());
        }

        if (command.memo() != null && !command.memo().equals(workItem.getMemo())) {
            changes.add(new FieldChange(
                    "memo",
                    workItem.getMemo(),
                    command.memo()
            ));
            workItem.updateMemo(command.memo());
        }

        if (command.tags() != null && !Objects.equals(command.tags(), workItem.getTags())) {
            changes.add(new FieldChange(
                    "tags",
                    workItem.getTags() == null ? "" : String.join(",", workItem.getTags()),
                    String.join(",", command.tags())
            ));
            workItem.updateTags(command.tags());
        }

        // 실제 동시성 충돌 검출은 repository.save() -> saveAndFlush() -> @Version 에서 발생
        WorkItem saved = workItemRepository.save(workItem);

        if (!changes.isEmpty()) {
            eventPublisher.publishEvent(
                    new WorkItemUpdatedEvent(saved.getId(), changes, "system")
            );
        }

        return saved;
    }
}