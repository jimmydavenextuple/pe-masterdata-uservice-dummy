package com.nextuple.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {

  @Value("${pe.ui.allowed-origin-url:}")
  private String allowedOrigin;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to init here
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    // If response stream is still open
    // Set the required response headers
    if (!response.isCommitted() && StringUtils.hasLength(allowedOrigin)) {
      var httpServletResponse = (HttpServletResponse) response;
      httpServletResponse.setHeader( // NOSONAR
          "Access-Control-Allow-Origin", allowedOrigin);
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
