package com.taxworkbench.api.interfaces.api.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record ApiError(
        String code,
        Map<String, Object> details
) {
    public ApiError {
        details = details == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public static ApiError of(String code) {
        return new ApiError(code, Map.of());
    }

    public static ApiError of(String code, Map<String, Object> details) {
        return new ApiError(code, details);
    }
}
