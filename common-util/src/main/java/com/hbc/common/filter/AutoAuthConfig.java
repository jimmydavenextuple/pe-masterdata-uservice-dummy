package com.hbc.common.filter;

import java.util.List;
import java.util.Map;
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

  @Value("#{${auth.claims:{'scope': 'sfcc-resources/edd,management-app/admin'}}}")
  private Map<String, List<String>> claims;

  @Value("${auth.issuer:https://cognito-idp.us-east-1.amazonaws.com/us-east-1_SRg9eldJN}")
  private String issuer;

  @Bean
  public AuthProperties authProperties() {
    var authProperties = new AuthProperties();
    authProperties.setFilterEnabled(filterEnabled);
    authProperties.setClaims(claims);
    authProperties.setIssuer(issuer);
    return authProperties;
  }
}
