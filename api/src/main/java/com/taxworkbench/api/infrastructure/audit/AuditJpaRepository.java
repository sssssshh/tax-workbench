package com.taxworkbench.api.infrastructure.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditJpaRepository extends JpaRepository<AuditLogJpaEntity, Long> {
    Page<AuditLogJpaEntity> findByEntityTypeAndEntityIdOrderByChangedAtDesc(
            String entityType, Long entityId, Pageable pageable);
}