package com.taxworkbench.api.domain.workitem;

import java.util.List;

public record WorkItemPage(
        List<WorkItem> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
) {}