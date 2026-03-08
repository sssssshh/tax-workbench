package com.taxworkbench.api.interfaces.api.client;

import com.taxworkbench.api.application.client.CreateClientUseCase;
import com.taxworkbench.api.application.client.QueryClientUseCase;
import com.taxworkbench.api.interfaces.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final QueryClientUseCase queryClientUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> findAll() {
        List<ClientResponse> responses = queryClientUseCase.findAll().stream()
                .map(ClientResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(@RequestBody ClientRequest request) {
        ClientResponse response = ClientResponse.from(
                createClientUseCase.execute(request.toCommand()));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}