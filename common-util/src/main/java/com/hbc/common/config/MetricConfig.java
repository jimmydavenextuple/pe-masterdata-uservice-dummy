package com.hbc.common.config;

import com.hbc.common.exception.ConfigException;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class MetricConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> {
      String hostname = System.getenv("HOSTNAME");
      if (!StringUtils.hasLength(hostname)) {
        try {
          hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
          throw new ConfigException(e.getCause().getMessage());
        }
      }
      registry.config().commonTags("host", hostname);
    };
  }
}
