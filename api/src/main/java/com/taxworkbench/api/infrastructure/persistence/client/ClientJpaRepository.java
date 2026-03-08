package com.taxworkbench.api.infrastructure.persistence.client;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, Long> {
    Optional<ClientJpaEntity> findByBizNo(String bizNo);
    boolean existsByBizNo(String bizNo);
}