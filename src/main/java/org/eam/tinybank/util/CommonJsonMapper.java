package org.eam.tinybank.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Encapsulates preconfigured shared instance of {@link ObjectMapper}, which doesn't fail on unknown properties.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonJsonMapper {

    public static final ObjectMapper INSTANCE = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

}