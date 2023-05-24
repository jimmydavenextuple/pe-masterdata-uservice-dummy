package com.nextuple.carrier.config;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarrierOptionConfig {
  @Value("#{'${carrier.service.options}'.split('\\s*,\\s*')}")
  public Set<String> serviceOptions;
}
