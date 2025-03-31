package com.nextuple.common.exception;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FirewallExceptionLoggingFilter extends OncePerRequestFilter {
  private static final Logger logger =
      LoggerFactory.getLogger(FirewallExceptionLoggingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (RequestRejectedException ex) {
      logger.info(
          "Incoming request: {} {} with the following exception :{}",
          request.getMethod(),
          request.getRequestURL(),
          ex.getCause());
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Blocked by security firewall");
    }
  }
}
