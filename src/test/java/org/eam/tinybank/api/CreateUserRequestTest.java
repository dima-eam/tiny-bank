package org.eam.tinybank.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class CreateUserRequestTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var request = new CreateUserRequest("test", "test", "test@test.com");
        var bytes = MAPPER.writeValueAsBytes(request);

        var deserialized = MAPPER.readValue(bytes, CreateUserRequest.class);
        assertEquals(request, deserialized);
    }

    @Test
    void shouldValidateEmail() {
        var valid = new CreateUserRequest("test", "test", "test@test.com");
        assertTrue(valid.validEmail());

        var invalid = new CreateUserRequest("test", "test", "test.com");
        assertFalse(invalid.validEmail());
    }

    @Test
    void shouldThrowNpeWithoutMandatoryField() {
        assertThrows(NullPointerException.class, () -> new CreateUserRequest("test", "test", null));
    }

}