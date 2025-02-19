package com.nextuple.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(
    prefix = "default-cors-filter",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class CORSFilter implements Filter {
  private final Environment environment;

  @Value("#{'${cors.allowed-environments:dev,qa,default}'.split(',')}")
  private String[] allowedEnvironments;

  private Boolean isEnvAllowed;

  public CORSFilter(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // Nothing to init here
  }

  // Check if the environment is allowed or not
  private boolean getIsEnvAllowed() {
    if (Objects.isNull(isEnvAllowed)) {
      isEnvAllowed =
          Arrays.stream(environment.getActiveProfiles())
              .anyMatch(profile -> Arrays.asList(allowedEnvironments).contains(profile));
    }
    return isEnvAllowed;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (!response.isCommitted() && getIsEnvAllowed()) {
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      // Allow all origins, headers, and methods for allowed environments
      httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
      httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
      httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // Nothing to destroy here
  }
}
