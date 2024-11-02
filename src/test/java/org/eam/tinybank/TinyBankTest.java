package org.eam.tinybank;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.util.CommonJsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests ALL application endpoints and their possible outcomes.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TinyBankTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Also tests that create endpoint is idempotent, and deactivate endpoint returns an error.
     */
    @Test
    void shouldCreateAndDeactivateUser() throws Exception {
        mockMvc.perform(put("/api/user/create").contentType(MediaType.APPLICATION_JSON_VALUE).content(user()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was created")));

        mockMvc.perform(put("/api/user/create").contentType(MediaType.APPLICATION_JSON_VALUE).content(user()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User exists")));

        mockMvc.perform(post("/api/user/deactivate?email=test@test.com"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was deactivated")));

        mockMvc.perform(post("/api/user/deactivate?email=test2@test.com"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("User not found")));
    }

    private String user() throws JsonProcessingException {
        return CommonJsonMapper.INSTANCE.writeValueAsString(new CreateUserRequest("test", "test", "test@test.com"));
    }

}