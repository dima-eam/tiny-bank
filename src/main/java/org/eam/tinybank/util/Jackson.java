package org.eam.tinybank.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Encapsulates preconfigured shared instance of {@link ObjectMapper}, which doesn't fail on unknown properties.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Jackson {

    public static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @SneakyThrows
    public static <T> String asString(@NonNull T value) {
        return MAPPER.writeValueAsString(Objects.requireNonNull(value, "Given value is null, can't serialize"));
    }

}