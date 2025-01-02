package com.nextuple.common.filter;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.LogContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.APIKeyHeaderNotFoundException;
import com.nextuple.common.exception.TenantIdHeaderNotFoundException;
import com.nextuple.common.util.CorrelationUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(CorrelationFilter.class);

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${spring.application.version}")
  private String applicationVersion;

  @Value("${spring.correlation-filter.failIfTenantIdNotPresent:false}")
  private boolean failIfTenantIdNotPresent;

  @Value("${spring.correlation-filter.failIfApiKeyNotPresent:false}")
  private boolean failIfApiKeyNotPresent;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    // Clean previous context
    CurrentThreadContext.cleanLogContext();
    MDC.clear();

    // Set Application name and version
    CurrentThreadContext.getLogContext()
        .initFromRequest(httpServletRequest)
        .setApplicationName(applicationName)
        .setApplicationVersion(applicationVersion);

    // Process correlation id
    CorrelationUtil.processCorrelationId(
        CorrelationUtil.extractCorrelationId((HttpServletRequest) request));

    // Check for Tenant ID Header
    if (failIfTenantIdNotPresent
        && ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getTenantId())) {
      throw new TenantIdHeaderNotFoundException();
    }

    // Check for API Key Header
    if (failIfApiKeyNotPresent
        && ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getApiKey())) {
      throw new APIKeyHeaderNotFoundException();
    }

    // Attach Correlation ID and Service Correlation ID in response headers
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (!response.isCommitted()) {
      httpServletResponse.setHeader(
          LogContext.CORRELATION_ID, CorrelationUtil.getCurrentCorrelationId());

      httpServletResponse.setHeader(
          LogContext.SERVICE_CORRELATION_ID, CorrelationUtil.getCurrentServiceCorrelationId());

      httpServletResponse.setHeader("X-App-Version", applicationVersion);
    }

    // Process request
    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      logger.error(e);
      throw e;
    } finally {
      // Clear context
      //      CurrentThreadContext.cleanLogContext();
      MDC.clear();
    }
  }
}
