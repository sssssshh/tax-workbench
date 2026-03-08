package com.taxworkbench.api.infrastructure.audit;

import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditRepositoryImpl implements AuditRepository {

    private final AuditJpaRepository jpaRepository;

    @Override
    public void save(AuditLog auditLog) {
        jpaRepository.save(AuditLogJpaEntity.fromDomain(auditLog));
    }

    @Override
    public void saveAll(List<AuditLog> auditLogs) {
        List<AuditLogJpaEntity> entities = auditLogs.stream()
                .map(AuditLogJpaEntity::fromDomain)
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId) {
        return jpaRepository
                .findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId)
                .stream()
                .map(AuditLogJpaEntity::toDomain)
                .toList();
    }
}