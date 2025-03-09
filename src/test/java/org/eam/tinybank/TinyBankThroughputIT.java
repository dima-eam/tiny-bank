package org.eam.tinybank;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.eam.tinybank.config.ThroughputTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ThroughputTestConfiguration.class)
@Log4j2
public class TinyBankThroughputIT {

    private final HttpClient client = HttpClient.newHttpClient();

    @SneakyThrows
    @Test
    void shouldRespondInTime() {
        String json = """
            {
                "firstname":"test",
                "lastname":"test",
                "email":"test@test.com"
            }
            """;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("http://localhost:8080/api/user/create"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info(response.body());
    }

}