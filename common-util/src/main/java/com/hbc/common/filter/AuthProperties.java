package com.hbc.common.filter;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Data
@ConfigurationProperties(prefix = "auth")
@ConfigurationPropertiesScan
public class AuthProperties {

  private boolean filterEnabled;
  private List<String> roles;
  private String issuer;
}
