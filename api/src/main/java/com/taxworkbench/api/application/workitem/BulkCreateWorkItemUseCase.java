package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.client.Client;
import com.taxworkbench.api.domain.client.ClientRepository;
import com.taxworkbench.api.domain.workitem.WorkItem;
import com.taxworkbench.api.domain.workitem.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BulkCreateWorkItemUseCase {

    private final WorkItemRepository workItemRepository;
    private final ClientRepository clientRepository;

    private static final int BATCH_SIZE = 500;

    @Transactional
    public int execute(List<CreateWorkItemCommand> commands) {
        List<Long> clientIds = commands.stream()
                .map(CreateWorkItemCommand::clientId)
                .distinct()
                .toList();

        Map<Long, Client> clientMap = clientRepository.findAllById(clientIds).stream()
                .collect(Collectors.toMap(Client::getId, c -> c));

        List<WorkItem> workItems = commands.stream()
                .map(command -> {
                    Client client = clientMap.get(command.clientId());
                    if (client == null || !client.isActive()) {
                        return null;
                    }

                    return WorkItem.create(
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
                })
                .filter(item -> item != null)
                .toList();

        int saved = 0;
        for (int i = 0; i < workItems.size(); i += BATCH_SIZE) {
            List<WorkItem> batch = workItems.subList(i, Math.min(i + BATCH_SIZE, workItems.size()));
            workItemRepository.saveAll(batch);
            saved += batch.size();
        }

        return saved;
    }
}