package com.hbc.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {

  @Autowired Environment environment;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to init here
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // If response stream is still open
    // Check for Dev environment
    if (!response.isCommitted() && environment != null) {
      Optional<String> devOptional =
          Arrays.stream(environment.getActiveProfiles())
              .map(String::toLowerCase)
              .filter(x -> "dev".equalsIgnoreCase(x) || "default".equalsIgnoreCase(x))
              .findAny();
      if (devOptional.isPresent()) {
        var httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader( // NOSONAR
            "Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // Nothing to destroy here
  }
}
