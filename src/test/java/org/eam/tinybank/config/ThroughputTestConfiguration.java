package org.eam.tinybank.config;

import org.eam.tinybank.config.properties.ThroughputTestProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ThroughputTestProperties.class)
public class ThroughputTestConfiguration {

}
