package com.taxworkbench.api.domain.workitem;

import java.util.List;
import java.util.Optional;

public interface WorkItemRepository {
    WorkItem save(WorkItem workItem);
    List<WorkItem> saveAll(List<WorkItem> workItems);
    Optional<WorkItem> findById(Long id);
    WorkItemPage findAll(WorkItemQuery query);
    List<WorkItem> findAllForExport(WorkItemQuery query);
    void deleteById(Long id);
}