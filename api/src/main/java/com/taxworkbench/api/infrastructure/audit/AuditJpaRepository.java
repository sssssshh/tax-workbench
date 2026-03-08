package com.taxworkbench.api.infrastructure.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditJpaRepository extends JpaRepository<AuditLogJpaEntity, Long> {
    List<AuditLogJpaEntity> findByEntityTypeAndEntityIdOrderByChangedAtDesc(
            String entityType, Long entityId);
}