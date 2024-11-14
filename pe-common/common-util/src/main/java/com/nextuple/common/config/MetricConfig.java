package com.nextuple.common.config;

import com.nextuple.common.exception.ConfigException;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class MetricConfig {
  public static final String CLUSTER_NAME = "CLUSTER_NAME";

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> {
      String hostname = System.getenv("HOSTNAME");
      String clusterName =
          Objects.nonNull(System.getenv(CLUSTER_NAME)) ? System.getenv(CLUSTER_NAME) : "local";
      if (!StringUtils.hasLength(hostname)) {
        try {
          hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
          throw new ConfigException(e.getCause().getMessage());
        }
      }

      registry.config().commonTags("clusterName", clusterName).commonTags("host", hostname);
    };
  }
}
