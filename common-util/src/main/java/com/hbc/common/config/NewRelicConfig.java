package com.hbc.common.config;

import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@AutoConfigureBefore({
  CompositeMeterRegistryAutoConfiguration.class,
  SimpleMetricsExportAutoConfiguration.class
})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass(NewRelicRegistry.class)
@Profile("!default")
public class NewRelicConfig {
  @Bean
  public NewRelicRegistry newRelicMeterRegistry(NewRelicRegistryConfig config) {
    var newRelicRegistry = NewRelicRegistry.builder(config).build();
    newRelicRegistry.config().meterFilter(MeterFilter.ignoreTags("plz_ignore_me"));
    newRelicRegistry.start(new NamedThreadFactory("newrelic.micrometer.registry"));
    return newRelicRegistry;
  }

  @Bean
  public NewRelicRegistryConfig customNewRelicConfig() {
    return new NewRelicRegistryConfig() {
      @Override
      public String get(String key) {
        if (key.contains("apiKey")) {
          return apiKey();
        }
        return null;
      }

      @Override
      public String apiKey() {
        return System.getenv("NEW_RELIC_LICENSE_KEY");
      }

      @Override
      public String serviceName() {
        return System.getenv("NEW_RELIC_APP_NAME");
      }
    };
  }
}
