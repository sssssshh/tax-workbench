package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.audit.AuditRepository;
import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        // Optimistic Lock 체크
        workItem.checkVersion(command.expectedVersion());

        // 변경 이력 수집
        List<FieldChange> changes = new ArrayList<>();

        if (command.status() != null && command.status() != workItem.getStatus()) {
            changes.add(new FieldChange("status",
                    workItem.getStatus().name(), command.status().name()));
            workItem.changeStatus(command.status());
        }
        if (command.assignee() != null && !command.assignee().equals(workItem.getAssignee())) {
            changes.add(new FieldChange("assignee",
                    workItem.getAssignee(), command.assignee()));
            workItem.updateAssignee(command.assignee());
        }
        if (command.dueDate() != null && !command.dueDate().equals(workItem.getDueDate())) {
            changes.add(new FieldChange("dueDate",
                    String.valueOf(workItem.getDueDate()), String.valueOf(command.dueDate())));
            workItem.updateDueDate(command.dueDate());
        }
        if (command.memo() != null && !command.memo().equals(workItem.getMemo())) {
            changes.add(new FieldChange("memo", workItem.getMemo(), command.memo()));
            workItem.updateMemo(command.memo());
        }

        workItem.incrementVersion();
        WorkItem saved = workItemRepository.save(workItem);

        // 변경 이력 이벤트 발행 (비동기 처리)
        if (!changes.isEmpty()) {
            eventPublisher.publishEvent(
                    new WorkItemUpdatedEvent(saved.getId(), changes, "system"));
        }

        return saved;
    }
}