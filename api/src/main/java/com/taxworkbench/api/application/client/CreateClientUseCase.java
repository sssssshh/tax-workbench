package com.taxworkbench.api.application.client;

import com.taxworkbench.api.domain.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateClientUseCase {

    private final ClientRepository clientRepository;

    @Transactional
    public Client execute(CreateClientCommand command) {
        if (clientRepository.existsByBizNo(command.bizNo())) {
            throw new IllegalArgumentException("이미 등록된 사업자번호입니다: " + command.bizNo());
        }

        Client client = Client.create(
                command.name(),
                command.bizNo(),
                command.type(),
                command.tier()
        );

        return clientRepository.save(client);
    }
}