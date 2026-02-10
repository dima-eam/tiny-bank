package org.eam.tinybank.config;

import org.eam.tinybank.config.properties.ThroughputTestProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties
@Import(ThroughputTestProperties.class)
public class ThroughputTestConfiguration {

}