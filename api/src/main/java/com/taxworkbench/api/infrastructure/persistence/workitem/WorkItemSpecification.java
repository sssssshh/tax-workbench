package com.taxworkbench.api.infrastructure.persistence.workitem;

import com.taxworkbench.api.domain.workitem.WorkItemQuery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class WorkItemSpecification {

    public static Specification<WorkItemJpaEntity> of(WorkItemQuery query) {
        List<Specification<WorkItemJpaEntity>> specs = new ArrayList<>();

        if (query.clientName() != null) {
            specs.add((root, q, cb) ->
                    cb.like(root.get("clientName"), "%" + query.clientName() + "%"));
        }
        if (query.status() != null) {
            specs.add((root, q, cb) ->
                    cb.equal(root.get("status").as(String.class), query.status().name()));
        }
        if (query.assignee() != null) {
            specs.add((root, q, cb) ->
                    cb.equal(root.get("assignee"), query.assignee()));
        }
        if (query.dueDateFrom() != null) {
            specs.add((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dueDate"), query.dueDateFrom()));
        }
        if (query.dueDateTo() != null) {
            specs.add((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("dueDate"), query.dueDateTo()));
        }

        if (specs.isEmpty()) {
            return (root, q, cb) -> cb.conjunction(); // 조건 없으면 전체 조회
        }

        return specs.stream().reduce((a, b) -> a.and(b)).orElseThrow();
    }
}