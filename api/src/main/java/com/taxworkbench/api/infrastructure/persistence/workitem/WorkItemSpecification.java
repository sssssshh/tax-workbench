package com.taxworkbench.api.infrastructure.persistence.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import org.springframework.data.jpa.domain.Specification;

public class WorkItemSpecification {

    public static Specification<WorkItemJpaEntity> of(WorkItemQuery query) {
        return Specification
                .where(likeClientName(query.clientName()))
                .and(equalStatus(query.status() != null ? query.status().name() : null))
                .and(equalAssignee(query.assignee()));
    }

    private static Specification<WorkItemJpaEntity> likeClientName(String clientName) {
        return (root, q, cb) -> clientName == null ? null :
                cb.like(root.get("clientName"), "%" + clientName + "%");
    }

    private static Specification<WorkItemJpaEntity> equalStatus(String status) {
        return (root, q, cb) -> status == null ? null :
                cb.equal(root.get("status").as(String.class), status);
    }

    private static Specification<WorkItemJpaEntity> equalAssignee(String assignee) {
        return (root, q, cb) -> assignee == null ? null :
                cb.equal(root.get("assignee"), assignee);
    }
}