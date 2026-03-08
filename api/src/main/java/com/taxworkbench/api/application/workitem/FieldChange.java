package com.taxworkbench.api.application.workitem;

public record FieldChange(
        String fieldName,
        String oldValue,
        String newValue
) {}