package com.taxworkbench.api.infrastructure.persistence.workitem;

import com.taxworkbench.api.domain.workitem.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "work_items", indexes = {
        @Index(name = "idx_work_items_client_id", columnList = "clientId"),
        @Index(name = "idx_work_items_status", columnList = "status"),
        @Index(name = "idx_work_items_assignee", columnList = "assignee"),
        @Index(name = "idx_work_items_due_date", columnList = "dueDate")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String clientName;

    private String bizNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemStatus status;

    private String assignee;
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String tags; // 콤마로 구분하여 저장

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private long version; // JPA Optimistic Lock

    public WorkItem toDomain() {
        WorkItem item = WorkItem.create(
                clientId, clientName, bizNo, type,
                assignee, dueDate, memo,
                null, null
        );
        item.setId(id);
        item.setVersion(version);
        item.setStatus(status);
        if (tags != null && !tags.isEmpty()) {
            item.updateTags(Arrays.asList(tags.split(",")));
        }
        return item;
    }

    public static WorkItemJpaEntity fromDomain(WorkItem item) {
        String tagsStr = item.getTags() == null ? "" :
                String.join(",", item.getTags());

        return WorkItemJpaEntity.builder()
                .id(item.getId())
                .clientId(item.getClientId())
                .clientName(item.getClientName())
                .bizNo(item.getBizNo())
                .type(item.getType())
                .status(item.getStatus())
                .assignee(item.getAssignee())
                .dueDate(item.getDueDate())
                .tags(tagsStr)
                .memo(item.getMemo())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .version(item.getVersion())
                .build();
    }

    public void update(WorkItem item) {
        System.out.println("=== ENTITY UPDATE ===");
        System.out.println("before status: " + this.status);
        this.status = item.getStatus();
        System.out.println("after status: " + this.status);
        this.assignee = item.getAssignee();
        this.dueDate = item.getDueDate();
        this.memo = item.getMemo();
        this.tags = item.getTags() == null ? "" : String.join(",", item.getTags());
        this.updatedAt = item.getUpdatedAt();
    }
}