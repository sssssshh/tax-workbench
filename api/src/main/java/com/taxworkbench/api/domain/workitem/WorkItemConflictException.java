package com.taxworkbench.api.domain.workitem;

public class WorkItemConflictException extends RuntimeException {

    private final long currentVersion;
    private final long expectedVersion;

    public WorkItemConflictException(long currentVersion, long expectedVersion) {
        super("다른 사용자가 이미 수정했습니다. 최신 데이터를 확인해주세요.");
        this.currentVersion = currentVersion;
        this.expectedVersion = expectedVersion;
    }

    public long getCurrentVersion() { return currentVersion; }
    public long getExpectedVersion() { return expectedVersion; }
}