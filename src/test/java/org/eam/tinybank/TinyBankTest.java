package org.eam.tinybank;

import static org.eam.tinybank.util.Jackson.asString;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.api.DepositRequest;
import org.eam.tinybank.api.TransferRequest;
import org.eam.tinybank.api.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests ALL application endpoints and their possible outcomes, generating a random email for each test execution. Some
 * tests are overlapping.
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

        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was created")));

        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User exists")));

        mockMvc.perform(patch("/api/user/deactivate?email=" + request.email()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User was deactivated")));

        mockMvc.perform(patch("/api/user/deactivate?email=test@test.com"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("User not found: email=test@test.com")));
    }

    /**
     * Also tests that create endpoint is idempotent.
     */
    @Test
    void shouldCreateUserAndAccount() throws Exception {
        var request = createUserRequest();

        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(request)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(request.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account was created")));

        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account exists")));
    }

    /**
     * Also tests validation and balance check.
     */
    @Test
    void shouldDepositAndWithdraw() throws Exception {
        var userRequest = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(userRequest.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk());

        var amount = BigDecimal.ZERO;
        var depositRequest = new DepositRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid amount")));

        amount = randomAmount();
        depositRequest = new DepositRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account was deposited: balance=" + amount)));

        amount = amount.subtract(BigDecimal.TEN);
        var withdrawRequest = new WithdrawRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/withdraw").contentType(APPLICATION_JSON_VALUE).content(asString(withdrawRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Account was withdrawed: balance=10.00")));

        amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP);
        withdrawRequest = new WithdrawRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/withdraw").contentType(APPLICATION_JSON_VALUE).content(asString(withdrawRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Insufficient funds")));

        amount = BigDecimal.ZERO;
        withdrawRequest = new WithdrawRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/withdraw").contentType(APPLICATION_JSON_VALUE).content(asString(withdrawRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Invalid amount")));
    }

    @Test
    void shouldTransferFunds() throws Exception {
        var userRequest1 = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest1)))
            .andExpect(status().isOk());

        var accountRequest1 = new CreateAccountRequest(userRequest1.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest1)))
            .andExpect(status().isOk());

        var amount = randomAmount();
        var depositRequest = new DepositRequest(userRequest1.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk());

        var userRequest2 = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest2)))
            .andExpect(status().isOk());

        var transferRequest = new TransferRequest(userRequest1.email(), userRequest2.email(), BigDecimal.TEN);
        mockMvc.perform( // Check error when no recipient account
                         post("/api/account/transfer").contentType(APPLICATION_JSON_VALUE)
                             .content(asString(transferRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Account not found: email=%s".formatted(userRequest2.email()))));

        transferRequest = new TransferRequest(userRequest2.email(), userRequest1.email(), BigDecimal.TEN);
        mockMvc.perform( // check error when no sender account
                         post("/api/account/transfer").contentType(APPLICATION_JSON_VALUE)
                             .content(asString(transferRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Account not found: email=%s".formatted(userRequest2.email()))));

        var accountRequest2 = new CreateAccountRequest(userRequest2.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest2)))
            .andExpect(status().isOk());

        transferRequest = new TransferRequest(userRequest1.email(), userRequest2.email(), BigDecimal.TEN);
        mockMvc.perform(
                post("/api/account/transfer").contentType(APPLICATION_JSON_VALUE).content(asString(transferRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Funds transferred: from=%s, to=%s"
                                                           .formatted(userRequest1.email(), userRequest2.email()))));

        mockMvc.perform(get("/api/account/balance?email=%s".formatted(userRequest1.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Balance: %s".formatted(amount.subtract(BigDecimal.TEN)))));

        mockMvc.perform(get("/api/account/history?email=%s".formatted(userRequest1.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("description=Transfer to %s, amount=10"
                                                           .formatted(userRequest2.email()))));

        mockMvc.perform(get("/api/account/history?email=%s".formatted(userRequest2.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("description=Receive from %s, amount=10"
                                                           .formatted(userRequest1.email()))));
    }

    @Test
    void shouldReturnCorrectBalanceInMultithreaded() throws Exception {
        var userRequest = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(userRequest.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk());

        var amount = BigDecimal.valueOf(100L);
        var depositRequest = new DepositRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk());

        var deposits = CompletableFuture.runAsync(
            () -> callMultiple("deposit", new DepositRequest(userRequest.email(), BigDecimal.ONE)));
        var withdraws = CompletableFuture.runAsync(
            () -> callMultiple("withdraw", new WithdrawRequest(userRequest.email(), BigDecimal.ONE)));
        CompletableFuture.allOf(deposits, withdraws).join();

        mockMvc.perform(get("/api/account/balance?email=%s".formatted(userRequest.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Balance: 100")));
    }

    @Test
    void shouldNotDeadlockInMultithreaded() throws Exception {
        var userRequest1 = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest1)))
            .andExpect(status().isOk());
        var userRequest2 = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest2)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(userRequest1.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk());
        accountRequest = new CreateAccountRequest(userRequest2.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk());

        var amount = BigDecimal.valueOf(100L);
        var depositRequest = new DepositRequest(userRequest1.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk());
        depositRequest = new DepositRequest(userRequest2.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk());

        var transfers1 = CompletableFuture.runAsync(
            () -> callMultiple("transfer", new TransferRequest(userRequest1.email(),
                                                               userRequest2.email(),
                                                               BigDecimal.ONE)));
        var transfers2 = CompletableFuture.runAsync(
            () -> callMultiple("transfer", new TransferRequest(userRequest2.email(),
                                                               userRequest1.email(),
                                                               BigDecimal.ONE)));
        CompletableFuture.allOf(transfers1, transfers2).join();

        mockMvc.perform(get("/api/account/balance?email=%s".formatted(userRequest1.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Balance: 100")));
        mockMvc.perform(get("/api/account/balance?email=%s".formatted(userRequest2.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Balance: 100")));
    }

    @Test
    void shouldReturnBalanceAndHistory() throws Exception {
        var userRequest = createUserRequest();
        mockMvc.perform(post("/api/user/create").contentType(APPLICATION_JSON_VALUE).content(asString(userRequest)))
            .andExpect(status().isOk());

        var accountRequest = new CreateAccountRequest(userRequest.email());
        mockMvc.perform(
                post("/api/account/create").contentType(APPLICATION_JSON_VALUE).content(asString(accountRequest)))
            .andExpect(status().isOk());

        var amount = BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP);
        var depositRequest = new DepositRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/deposit").contentType(APPLICATION_JSON_VALUE).content(asString(depositRequest)))
            .andExpect(status().isOk());

        amount = BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP);
        var withdrawRequest = new WithdrawRequest(userRequest.email(), amount);
        mockMvc.perform(
                post("/api/account/withdraw").contentType(APPLICATION_JSON_VALUE).content(asString(withdrawRequest)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/account/balance?email=%s".formatted(userRequest.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Balance: 990.00")));

        mockMvc.perform(get("/api/account/history?email=%s".formatted(userRequest.email())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("description=Deposit: 1000.00, amount=1000.00")))
            .andExpect(content().string(containsString("description=Withdraw: 10.00, amount=10.00")));
    }

    private static CreateUserRequest createUserRequest() {
        return new CreateUserRequest("test", "test", RandomStringUtils.randomAlphabetic(10) + "@test.com");
    }

    private static BigDecimal randomAmount() {
        return BigDecimal.valueOf(10 + RANDOM.nextDouble() * 1000).setScale(2, RoundingMode.HALF_UP);
    }

    @SneakyThrows
    private void callMultiple(String path, Object request) {
        for (var i = 0; i < 100; i++) {
            mockMvc.perform(
                post("/api/account/" + path).contentType(APPLICATION_JSON_VALUE)
                    .content(asString(request)));
        }
    }

}