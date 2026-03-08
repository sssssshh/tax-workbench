package com.taxworkbench.api.application.client;

import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;

public record CreateClientCommand(
        String name,
        String bizNo,
        ClientType type,
        ClientTier tier
) {}