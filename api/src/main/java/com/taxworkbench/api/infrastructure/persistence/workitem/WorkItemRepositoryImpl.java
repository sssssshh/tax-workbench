package com.taxworkbench.api.infrastructure.persistence.workitem;

import com.taxworkbench.api.domain.workitem.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkItemRepositoryImpl implements WorkItemRepository {

    private final WorkItemJpaRepository jpaRepository;

    @Override
    public WorkItem save(WorkItem workItem) {
        WorkItemJpaEntity entity = WorkItemJpaEntity.fromDomain(workItem);
        WorkItemJpaEntity saved = jpaRepository.saveAndFlush(entity);
        return saved.toDomain();
    }

    @Override
    public List<WorkItem> saveAll(List<WorkItem> workItems) {
        List<WorkItemJpaEntity> entities = workItems.stream()
                .map(WorkItemJpaEntity::fromDomain)
                .toList();

        return jpaRepository.saveAll(entities).stream()
                .map(WorkItemJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<WorkItem> findById(Long id) {
        return jpaRepository.findById(id).map(WorkItemJpaEntity::toDomain);
    }

    @Override
    public WorkItemPage findAll(WorkItemQuery query) {
        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                query.sortDir().equalsIgnoreCase("asc")
                        ? Sort.by(query.sortBy()).ascending()
                        : Sort.by(query.sortBy()).descending()
        );

        Specification<WorkItemJpaEntity> spec = WorkItemSpecification.of(query);
        Page<WorkItemJpaEntity> page = jpaRepository.findAll(spec, pageable);

        return new WorkItemPage(
                page.getContent().stream().map(WorkItemJpaEntity::toDomain).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkItem> findAllForExport(WorkItemQuery query) {
        Specification<WorkItemJpaEntity> spec = WorkItemSpecification.of(query);
        Sort sort = query.sortDir().equalsIgnoreCase("asc")
                ? Sort.by(query.sortBy()).ascending()
                : Sort.by(query.sortBy()).descending();

        return jpaRepository.findAll(spec, sort).stream()
                .map(WorkItemJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}