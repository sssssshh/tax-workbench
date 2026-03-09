package com.taxworkbench.api.interfaces.api.workitem;

import com.taxworkbench.api.application.workitem.BulkCreateResult;

import java.util.List;

public record BulkCreateResponse(
        int savedCount,
        int skippedCount,
        List<String> errors
) {
    public static BulkCreateResponse from(BulkCreateResult result) {
        return new BulkCreateResponse(
                result.savedCount(),
                result.skippedCount(),
                result.errors()
        );
    }
}