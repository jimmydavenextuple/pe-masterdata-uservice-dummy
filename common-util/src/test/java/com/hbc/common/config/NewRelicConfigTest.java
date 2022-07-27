package com.hbc.common.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NewRelicConfigTest {

  @InjectMocks NewRelicConfig newRelicConfig;

  @Mock NewRelicRegistryConfig config;

  @Test
  void newRelicMeterRegistry() {

    when(config.apiKey()).thenReturn("API_KEY");
    when(config.useLicenseKey()).thenReturn(false);
    Assertions.assertDoesNotThrow(() -> newRelicConfig.newRelicMeterRegistry(config));
  }

  @Test
  void customNewRelicConfig() {
    Assertions.assertDoesNotThrow(() -> newRelicConfig.customNewRelicConfig());
  }
}
