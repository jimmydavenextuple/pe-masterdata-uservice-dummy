package com.hbc.service.inventory.config;

import com.newrelic.telemetry.Attributes;
import io.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.newrelic.NewRelicRegistry;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
  public NewRelicRegistry newRelicMeterRegistry(NewRelicRegistryConfig config)
      throws UnknownHostException {
    NewRelicRegistry newRelicRegistry =
        NewRelicRegistry.builder(config)
            .commonAttributes(
                new Attributes().put("host", InetAddress.getLocalHost().getHostName()))
            .build();
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
