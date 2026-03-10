package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class WorkItemQueryRequest {

    private String clientName;
    private WorkItemStatus status;
    private String assignee;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateTo;

    @Min(value = 0, message = "page는 0 이상이어야 합니다.")
    private int page = 0;

    @Positive(message = "size는 1 이상이어야 합니다.")
    private int size = 20;

    private String sortBy = "createdAt";
    private String sortDir = "desc";

    public WorkItemQuery toQuery() {
        return new WorkItemQuery(
                clientName,
                status,
                assignee,
                dueDateFrom,
                dueDateTo,
                page,
                size,
                sortBy,
                sortDir
        );
    }

    public WorkItemQuery toExportQuery() {
        return new WorkItemQuery(
                clientName,
                status,
                assignee,
                dueDateFrom,
                dueDateTo,
                0,
                Integer.MAX_VALUE,
                sortBy,
                sortDir
        );
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public WorkItemStatus getStatus() {
        return status;
    }

    public void setStatus(WorkItemStatus status) {
        this.status = status;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public LocalDate getDueDateFrom() {
        return dueDateFrom;
    }

    public void setDueDateFrom(LocalDate dueDateFrom) {
        this.dueDateFrom = dueDateFrom;
    }

    public LocalDate getDueDateTo() {
        return dueDateTo;
    }

    public void setDueDateTo(LocalDate dueDateTo) {
        this.dueDateTo = dueDateTo;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
