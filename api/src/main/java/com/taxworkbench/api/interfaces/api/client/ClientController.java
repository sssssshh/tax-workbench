package com.taxworkbench.api.interfaces.api.client;

import com.taxworkbench.api.application.client.CreateClientUseCase;
import com.taxworkbench.api.application.client.QueryClientUseCase;
import com.taxworkbench.api.interfaces.api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final QueryClientUseCase queryClientUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<ClientListResponse>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(
                ClientListResponse.from(queryClientUseCase.findAll())
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(
            @Valid @RequestBody ClientRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                ClientResponse.from(createClientUseCase.execute(request.toCommand()))
        ));
    }
}
