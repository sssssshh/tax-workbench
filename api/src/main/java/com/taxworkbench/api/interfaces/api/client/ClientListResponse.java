package com.taxworkbench.api.interfaces.api.client;

import com.taxworkbench.api.domain.client.Client;

import java.util.List;

public record ClientListResponse(
        List<ClientResponse> items,
        int count
) {
    public static ClientListResponse from(List<Client> clients) {
        return new ClientListResponse(
                clients.stream()
                        .map(ClientResponse::from)
                        .toList(),
                clients.size()
        );
    }
}
