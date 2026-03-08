package com.taxworkbench.api.infrastructure.persistence.client;

import com.taxworkbench.api.domain.client.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClientJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String bizNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientTier tier;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // JPA Entity → Domain 변환
    public Client toDomain() {
        Client client = Client.create(name, bizNo, type, tier);
        client.setId(id);
        return client;
    }

    // Domain → JPA Entity 변환
    public static ClientJpaEntity fromDomain(Client client) {
        return ClientJpaEntity.builder()
                .id(client.getId())
                .name(client.getName())
                .bizNo(client.getBizNo())
                .type(client.getType())
                .status(client.getStatus())
                .tier(client.getTier())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}