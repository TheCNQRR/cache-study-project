package by.java.enterprise.dto.request;

import jakarta.annotation.Nullable;

public record UpdateUserPartRequest(
        @Nullable
        String username,

        @Nullable
        String displayName
) {
}
