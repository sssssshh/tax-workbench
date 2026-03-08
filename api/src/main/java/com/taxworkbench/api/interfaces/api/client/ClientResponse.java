package com.taxworkbench.api.interfaces.api.client;

import com.taxworkbench.api.domain.client.Client;
import com.taxworkbench.api.domain.client.ClientStatus;
import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;

import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String name,
        String bizNo,
        ClientType type,
        ClientStatus status,
        ClientTier tier,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getBizNo(),
                client.getType(),
                client.getStatus(),
                client.getTier(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}