package org.eam.tinybank.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "throughput")
@Data
public class ThroughputTestProperties {

    private int profilesCount;
    private int requestsPerSecond;
    private int durationInSeconds;
    private int maxLatencyMs;

}
