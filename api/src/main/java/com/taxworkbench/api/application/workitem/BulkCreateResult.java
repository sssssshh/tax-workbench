package com.taxworkbench.api.application.workitem;

import java.util.List;

public record BulkCreateResult(
        int savedCount,
        int skippedCount,
        List<String> errors
) {
}