package org.eam.tinybank;

import static org.assertj.core.api.Fail.fail;
import static org.eam.tinybank.util.Jackson.MAPPER;

import com.google.common.util.concurrent.RateLimiter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.eam.tinybank.api.CreateAccountRequest;
import org.eam.tinybank.api.CreateUserRequest;
import org.eam.tinybank.api.DepositRequest;
import org.eam.tinybank.config.ThroughputTestConfiguration;
import org.eam.tinybank.config.properties.ThroughputTestProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/**
 * Generates transactions and calculates latencies.
 * <p>
 * Example for H2 store with service running locally: Latencies: averageMs=2.0566666666666666, maxMs = 7.0
 */
@SpringBootTest(classes = ThroughputTestConfiguration.class)
@Log4j2
public class TinyBankThroughputIT {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private ThroughputTestProperties properties;

    @Test
    void shouldRespondInTime() {
        var emails = createdProfilesAndAccounts();
        var latencies = generatedTransactions(emails);
        checkLatencies(latencies);
    }

    @SneakyThrows
    private ArrayList<String> createdProfilesAndAccounts() {
        log.info("Creating profiles and accounts: count={}", properties.getProfilesCount());
        var emails = new ArrayList<String>();

        for (var i = 0; i < properties.getProfilesCount(); i++) {
            var userRequest = new CreateUserRequest("test", "test",
                                                    "%s@test.com".formatted(RandomStringUtils.randomAlphabetic(8)));

            var request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/user/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(userRequest)))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

            var accountRequest = new CreateAccountRequest(userRequest.email());
            request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/account/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(accountRequest)))
                .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

            var depositRequest = new DepositRequest(userRequest.email(),
                                                    BigDecimal.valueOf(RandomUtils.nextInt(5000, 10_000)));
            request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/account/deposit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(depositRequest)))
                .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(HttpStatus.OK.value(), response.statusCode());

            emails.add(userRequest.email());
        }

        return emails;
    }

    @SneakyThrows
    private List<Long> generatedTransactions(List<String> emails) {
        log.info("Generating transactions: durationInSeconds={}, requestsPerSecond={}",
                 properties.getDurationInSeconds(), properties.getRequestsPerSecond());
        var latencies = new ArrayList<Long>();

        var total = properties.getRequestsPerSecond() * properties.getDurationInSeconds();
        var count = 0;
        var limiter = RateLimiter.create(properties.getRequestsPerSecond());
        while (count++ < total) {
            limiter.acquire();
            var started = System.currentTimeMillis();
// TODO multithreaded deposit/withdraw/balance requests
            var depositRequest = new DepositRequest(emails.get(RandomUtils.nextInt(0, emails.size())),
                                                    BigDecimal.valueOf(RandomUtils.nextDouble(0, 100)));
            var request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/account/deposit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(depositRequest)))
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                fail("Service call failed: response={}", response.body());
            }

            if (count % 100 == 0) {
                log.info("Sent and received: count={}", count);
            }

            latencies.add(System.currentTimeMillis() - started);
        }

        return latencies;
    }

    private void checkLatencies(List<Long> latencies) {
        double avg = latencies.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElseThrow();
        double max = latencies.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElseThrow();
        log.info("Latencies: averageMs={}, maxMs = {}", avg, max);

        Assertions.assertTrue(
            avg < properties.getMaxLatencyMs(),
            "Average latency is too high, should be below %s ms".formatted(properties.getMaxLatencyMs())
        );
    }

}