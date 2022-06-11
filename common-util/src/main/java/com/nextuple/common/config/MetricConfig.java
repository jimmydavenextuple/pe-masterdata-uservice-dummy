package com.nextuple.common.config;

import com.nextuple.common.context.CurrentThreadContext;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry ->
        registry.config().commonTags("host", CurrentThreadContext.getLogContext().getHostName());
  }
}
