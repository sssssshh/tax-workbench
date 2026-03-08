package com.taxworkbench.api.interfaces.api.client;

import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;
import com.taxworkbench.api.application.client.CreateClientCommand;

public record ClientRequest(
        String name,
        String bizNo,
        ClientType type,
        ClientTier tier
) {
    public CreateClientCommand toCommand() {
        return new CreateClientCommand(name, bizNo, type, tier);
    }
}