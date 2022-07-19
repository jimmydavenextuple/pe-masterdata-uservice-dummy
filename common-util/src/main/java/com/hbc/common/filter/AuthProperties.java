package com.hbc.common.filter;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationProperties(prefix = "auth")
@ConfigurationPropertiesScan
public class AuthProperties {

  private boolean filterEnabled;
  private Map<String, String> claims;
  private String issuer;
}
