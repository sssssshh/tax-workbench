package com.taxworkbench.api.interfaces.api.client;

import com.taxworkbench.api.application.client.CreateClientCommand;
import com.taxworkbench.api.domain.client.ClientTier;
import com.taxworkbench.api.domain.client.ClientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank(message = "name은 필수입니다.")
        @Size(max = 100, message = "name은 100자를 초과할 수 없습니다.")
        String name,

        @NotBlank(message = "bizNo는 필수입니다.")
        @Pattern(
                regexp = "^\\d{3}-?\\d{2}-?\\d{5}$",
                message = "bizNo 형식이 올바르지 않습니다."
        )
        String bizNo,

        @NotNull(message = "type은 필수입니다.")
        ClientType type,

        @NotNull(message = "tier는 필수입니다.")
        ClientTier tier
) {
    public CreateClientCommand toCommand() {
        return new CreateClientCommand(name, bizNo, type, tier);
    }
}
