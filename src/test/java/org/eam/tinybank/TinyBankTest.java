package org.eam.tinybank;

import static org.eam.tinybank.util.CommonJsonMapper.asString;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.api.DepositRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests ALL application endpoints and their possible outcomes, generating a random email for each test execution.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TinyBankTest {

    private static final Random RANDOM = new Random();

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
            .andExpect(content().string(containsString("User not found: email=test@test.com")));
    }

    /**
     * Also tests that create endpoint is idempotent.
     */
    @Test
    void shouldCreateUserAndAccount() throws Exception {
        var request = createUserRequest();

        mockMvc.perform(put("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(request.email());
        mockMvc.perform(
                put("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account was created")));

        mockMvc.perform(
                put("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account exists")));
    }

    @Test
    void shouldDepositAndWithdraw() throws Exception {
        var userRequest = createUserRequest();
        mockMvc.perform(put("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(userRequest.email());
        mockMvc.perform(
                put("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk());

        var amount = BigDecimal.valueOf(RANDOM.nextDouble() * 100);
        var depositRequest = new DepositRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account was deposited")));
    }

    private static CreateUserRequest createUserRequest() {
        return new CreateUserRequest("test", "test", RandomStringUtils.randomAlphabetic(10) + "@test.com");
    }

}