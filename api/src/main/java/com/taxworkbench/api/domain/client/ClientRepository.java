package com.taxworkbench.api.domain.client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
    Optional<Client> findByBizNo(String bizNo);
    List<Client> findAll();
    boolean existsByBizNo(String bizNo);
}