package com.taxworkbench.api.domain.workitem;

public class WorkItemConflictException extends RuntimeException {

    private final Long workItemId;
    private final long currentVersion;
    private final long expectedVersion;

    public WorkItemConflictException(Long workItemId, long currentVersion, long expectedVersion) {
        super("다른 사용자가 이미 수정했습니다. 최신 데이터를 확인해주세요.");
        this.workItemId = workItemId;
        this.currentVersion = currentVersion;
        this.expectedVersion = expectedVersion;
    }

    public Long getWorkItemId() { return workItemId; }
    public long getCurrentVersion() { return currentVersion; }
    public long getExpectedVersion() { return expectedVersion; }
}