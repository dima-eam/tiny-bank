package org.eam.tinybank;

import static org.eam.tinybank.util.CommonJsonMapper.asString;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.RandomStringUtils;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
        var request = createUserRequest();

        mockMvc.perform(put("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was created")));

        mockMvc.perform(put("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User exists")));

        mockMvc.perform(post("/api/user/deactivate?email=" + request.email()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was deactivated")));

        mockMvc.perform(post("/api/user/deactivate?email=test@test.com"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("User not found")));
    }

    /**
     * Also tests that create endpoint is idempotent.
     */
    @Test
    void shouldCreateUserAndAccount() throws Exception {
        var request = createUserRequest();

        mockMvc.perform(put("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was created")));

        CreateAccountRequest accountRequest = new CreateAccountRequest(request.email());
        mockMvc.perform(
                put("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account was created")));

        mockMvc.perform(
                put("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account exists")));
    }

    private static CreateUserRequest createUserRequest() {
        return new CreateUserRequest("test", "test", RandomStringUtils.randomAlphabetic(10) + "@test.com");
    }

}