package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.client.Client;
import com.taxworkbench.api.domain.client.ClientRepository;
import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateWorkItemUseCase {

    private final WorkItemRepository workItemRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public WorkItem execute(CreateWorkItemCommand command) {
        // Client 조회 - tier/type 규칙 적용을 위해
        Client client = clientRepository.findById(command.clientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 고객입니다: " + command.clientId()));

        if (!client.isActive()) {
            throw new IllegalStateException("비활성화된 고객에게는 업무를 생성할 수 없습니다.");
        }

        WorkItem workItem = WorkItem.create(
                client.getId(),
                client.getName(),
                client.getBizNo(),
                command.type(),
                command.assignee(),
                command.dueDate(),
                command.memo(),
                client.getTier(),
                client.getType()
        );

        if (command.tags() != null) {
            workItem.updateTags(command.tags());
        }

        return workItemRepository.save(workItem);
    }
}