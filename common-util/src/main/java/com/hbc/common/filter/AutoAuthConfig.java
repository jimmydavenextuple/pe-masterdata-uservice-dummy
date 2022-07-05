package com.hbc.common.filter;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "auth",
    name = "filterEnabled",
    havingValue = "false",
    matchIfMissing = true)
public class AutoAuthConfig {

  @Value("${auth.filter-enabled:true}")
  private boolean filterEnabled;

  @Value("${auth.roles:ADMIN}")
  private List<String> roles;

  @Value("${auth.issuer:https://pe-dev-issuer.s3.amazonaws.com/}")
  private String issuer;

  @Bean
  public AuthProperties authProperties() {
    AuthProperties authProperties = new AuthProperties();
    authProperties.setFilterEnabled(filterEnabled);
    authProperties.setRoles(roles);
    authProperties.setIssuer(issuer);
    return authProperties;
  }
}
