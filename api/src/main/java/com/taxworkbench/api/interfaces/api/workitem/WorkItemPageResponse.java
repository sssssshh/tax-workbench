package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemPage;

import java.util.List;

public record WorkItemPageResponse(
        List<WorkItemResponse> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int size
) {
    public static WorkItemPageResponse from(WorkItemPage page) {
        return new WorkItemPageResponse(
                page.content().stream().map(WorkItemResponse::from).toList(),
                page.totalElements(),
                page.totalPages(),
                page.currentPage(),
                page.size()
        );
    }
}
