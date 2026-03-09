package com.taxworkbench.api.infrastructure.persistence.client;

import com.taxworkbench.api.domain.client.Client;
import com.taxworkbench.api.domain.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    @Override
    public Client save(Client client) {
        ClientJpaEntity entity = ClientJpaEntity.fromDomain(client);
        ClientJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id).map(ClientJpaEntity::toDomain);
    }

    @Override
    public Optional<Client> findByBizNo(String bizNo) {
        return jpaRepository.findByBizNo(bizNo).map(ClientJpaEntity::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream()
                .map(ClientJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Client> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(ClientJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByBizNo(String bizNo) {
        return jpaRepository.existsByBizNo(bizNo);
    }
}