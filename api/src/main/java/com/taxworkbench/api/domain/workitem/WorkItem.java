package com.taxworkbench.api.domain.workitem;

import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkItem {

    private Long id;
    private Long clientId;
    private String clientName;
    private String bizNo;
    private WorkItemType type;
    private WorkItemStatus status;
    private String assignee;
    private LocalDate dueDate;
    private List<String> tags;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long version;

    public static WorkItem create(
            Long clientId,
            String clientName,
            String bizNo,
            WorkItemType type,
            String assignee,
            LocalDate dueDate,
            String memo,
            ClientTier clientTier,
            ClientType clientType
    ) {
        validateCreation(type, clientTier, clientType);

        WorkItem item = new WorkItem();
        item.clientId = clientId;
        item.clientName = clientName;
        item.bizNo = bizNo;
        item.type = type;
        item.status = WorkItemStatus.TODO;
        item.assignee = assignee;
        item.dueDate = dueDate;
        item.memo = memo;
        item.tags = new ArrayList<>();
        item.createdAt = LocalDateTime.now();
        item.updatedAt = LocalDateTime.now();
        item.version = 0L;
        return item;
    }

    public static WorkItem restore(
            Long id,
            Long clientId,
            String clientName,
            String bizNo,
            WorkItemType type,
            WorkItemStatus status,
            String assignee,
            LocalDate dueDate,
            List<String> tags,
            String memo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        WorkItem item = new WorkItem();
        item.id = id;
        item.clientId = clientId;
        item.clientName = clientName;
        item.bizNo = bizNo;
        item.type = type;
        item.status = status;
        item.assignee = assignee;
        item.dueDate = dueDate;
        item.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
        item.memo = memo;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        item.version = version;
        return item;
    }

    private static void validateCreation(WorkItemType type, ClientTier tier, ClientType clientType) {
        if (tier == ClientTier.VIP && type == WorkItemType.ETC) {
            throw new IllegalArgumentException("VIP 고객에게는 기타(ETC) 업무를 할당할 수 없습니다.");
        }
        if (clientType == ClientType.INDIVIDUAL && type == WorkItemType.REVIEW) {
            throw new IllegalArgumentException("개인사업자 고객에게는 검토(REVIEW) 업무를 할당할 수 없습니다.");
        }
    }

    public void changeStatus(WorkItemStatus newStatus) {
        if (this.status == WorkItemStatus.DONE && newStatus == WorkItemStatus.TODO) {
            throw new IllegalStateException("완료된 업무는 TODO로 되돌릴 수 없습니다.");
        }
        this.status = newStatus;
        touch();
    }

    public void updateAssignee(String assignee) {
        this.assignee = assignee;
        touch();
    }

    public void updateDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        touch();
    }

    public void updateMemo(String memo) {
        this.memo = memo;
        touch();
    }

    public void updateTags(List<String> tags) {
        this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
        touch();
    }

    public void checkVersion(long expectedVersion) {
        if (this.version != expectedVersion) {
            throw new WorkItemConflictException(this.id, this.version, expectedVersion);
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public String getBizNo() { return bizNo; }
    public WorkItemType getType() { return type; }
    public WorkItemStatus getStatus() { return status; }
    public String getAssignee() { return assignee; }
    public LocalDate getDueDate() { return dueDate; }
    public List<String> getTags() { return tags; }
    public String getMemo() { return memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}