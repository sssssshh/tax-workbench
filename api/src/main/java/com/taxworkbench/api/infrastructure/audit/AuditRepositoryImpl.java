package com.taxworkbench.api.infrastructure.audit;

import com.taxworkbench.api.domain.audit.AuditLog;
import com.taxworkbench.api.domain.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
        // 최신 100건만 조회 - 수백만 건 쌓여도 성능 일정
        return jpaRepository
                .findByEntityTypeAndEntityIdOrderByChangedAtDesc(
                        entityType, entityId, PageRequest.of(0, 100))
                .getContent()
                .stream()
                .map(AuditLogJpaEntity::toDomain)
                .toList();
    }
}