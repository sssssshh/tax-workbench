package com.taxworkbench.api.infrastructure.persistence.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.stream.Stream;

public interface WorkItemJpaRepository extends
        JpaRepository<WorkItemJpaEntity, Long>,
        JpaSpecificationExecutor<WorkItemJpaEntity> {

    // 스트리밍 Export용 - 메모리 효율적
    @Query("SELECT w FROM WorkItemJpaEntity w WHERE "
            + "(:clientName IS NULL OR w.clientName LIKE %:clientName%) AND "
            + "(:status IS NULL OR w.status = :status) AND "
            + "(:assignee IS NULL OR w.assignee = :assignee)")
    Stream<WorkItemJpaEntity> streamForExport(
            @Param("clientName") String clientName,
            @Param("status") WorkItemStatus status,
            @Param("assignee") String assignee
    );
}