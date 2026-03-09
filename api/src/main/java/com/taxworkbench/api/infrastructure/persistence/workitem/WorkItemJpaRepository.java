package com.taxworkbench.api.infrastructure.persistence.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.QueryHint;
import java.util.stream.Stream;

public interface WorkItemJpaRepository extends
        JpaRepository<WorkItemJpaEntity, Long>,
        JpaSpecificationExecutor<WorkItemJpaEntity> {

    @Query("SELECT w FROM WorkItemJpaEntity w WHERE "
            + "(:clientName IS NULL OR w.clientName LIKE %:clientName%) AND "
            + "(:status IS NULL OR w.status = :status) AND "
            + "(:assignee IS NULL OR w.assignee = :assignee)")
    @QueryHints(value = {
            @QueryHint(name = "org.hibernate.fetchSize", value = "1000"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true")
    })
    Stream<WorkItemJpaEntity> streamForExport(
            @Param("clientName") String clientName,
            @Param("status") WorkItemStatus status,
            @Param("assignee") String assignee
    );
}