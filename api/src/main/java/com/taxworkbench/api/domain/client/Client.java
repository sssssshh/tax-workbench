package com.taxworkbench.api.domain.client;

import java.time.LocalDateTime;

public class Client {

    private Long id;
    private String name;
    private String bizNo;
    private ClientType type;
    private ClientStatus status;
    private ClientTier tier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 정적 팩토리 메서드
    public static Client create(String name, String bizNo, ClientType type, ClientTier tier) {
        Client client = new Client();
        client.name = name;
        client.bizNo = bizNo;
        client.type = type;
        client.tier = tier;
        client.status = ClientStatus.ACTIVE;
        client.createdAt = LocalDateTime.now();
        client.updatedAt = LocalDateTime.now();
        return client;
    }

    // 비즈니스 규칙: 비활성화
    public void deactivate() {
        if (this.status == ClientStatus.INACTIVE) {
            throw new IllegalStateException("이미 비활성화된 고객입니다.");
        }
        this.status = ClientStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 규칙: tier 변경
    public void changeTier(ClientTier newTier) {
        this.tier = newTier;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isVip() {
        return this.tier == ClientTier.VIP;
    }

    public boolean isActive() {
        return this.status == ClientStatus.ACTIVE;
    }

    // Getters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public String getBizNo() { return bizNo; }
    public ClientType getType() { return type; }
    public ClientStatus getStatus() { return status; }
    public ClientTier getTier() { return tier; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}