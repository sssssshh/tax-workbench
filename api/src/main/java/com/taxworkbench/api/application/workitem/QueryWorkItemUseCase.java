package com.taxworkbench.api.application.workitem;

import com.taxworkbench.api.domain.workitem.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryWorkItemUseCase {

    private final WorkItemRepository workItemRepository;

    @Transactional(readOnly = true)
    public WorkItemPage execute(WorkItemQuery query) {
        return workItemRepository.findAll(query);
    }

    @Transactional(readOnly = true)
    public WorkItem findById(Long id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 업무입니다: " + id));
    }
}