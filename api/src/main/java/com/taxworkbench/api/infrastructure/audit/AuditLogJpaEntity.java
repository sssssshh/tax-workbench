package com.taxworkbench.api.infrastructure.audit;

import com.taxworkbench.api.domain.audit.AuditLog;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entityType, entityId")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuditLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String fieldName;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    private String changedBy;
    private LocalDateTime changedAt;

    public static AuditLogJpaEntity fromDomain(AuditLog log) {
        return AuditLogJpaEntity.builder()
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .fieldName(log.getFieldName())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .changedBy(log.getChangedBy())
                .changedAt(log.getChangedAt())
                .build();
    }

    public AuditLog toDomain() {
        return AuditLog.of(entityType, entityId, fieldName, oldValue, newValue, changedBy);
    }
}