package com.nextuple.common.interceptor;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.LogContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {

  @Value("${api-key}")
  private String apiKey;

  @Value("${trusted-sites}")
  private String trustedSites;

  @Override
  public void apply(RequestTemplate requestTemplate) {

    log.debug("------ Inside FeignClientInterceptor ------");
    String[] trustedSitesSubstrings = trustedSites.split(",");
    try {
      String url = requestTemplate.feignTarget().url();
      boolean isTrustedSite = Arrays.stream(trustedSitesSubstrings).anyMatch(url::contains);
      if (url.contains("localhost") || isTrustedSite) {
        requestTemplate.header("x-api-key", apiKey);
      }
    } catch (Exception e) {
      log.info("Error while fetching host or api key");
    }

    // Attach Authorization header
    if (!requestTemplate.headers().containsKey(CommonConstants.AUTHORIZATION_HEADER)
        && Objects.nonNull(CurrentThreadContext.getLogContext().getRequestHeaders())) {
      requestTemplate.header(
          CommonConstants.AUTHORIZATION_HEADER,
          CurrentThreadContext.getLogContext().getAuthorizationHeader());
    }

    // Attach Tenant ID
    if (!requestTemplate.headers().containsKey(CommonConstants.HEADER_TENANT_ID)) {
      requestTemplate.header(
          CommonConstants.HEADER_TENANT_ID, CurrentThreadContext.getLogContext().getTenantId());
    }

    // Attach Correlation ID
    if (!requestTemplate.headers().containsKey(LogContext.CORRELATION_ID)) {
      requestTemplate.header(
          LogContext.CORRELATION_ID, CurrentThreadContext.getLogContext().getCorrelationId());
    }

    // Attach User
    if (!requestTemplate.headers().containsKey(CommonConstants.HEADER_USER)) {
      requestTemplate.header(
          CommonConstants.HEADER_USER, CurrentThreadContext.getLogContext().getUsername());
    }

    // Attach User Role
    if (!requestTemplate.headers().containsKey(CommonConstants.HEADER_ROLE)) {
      requestTemplate.header(
          CommonConstants.HEADER_ROLE, CurrentThreadContext.getLogContext().getUserRole());
    }

    // Attach User Locale
    if (!requestTemplate.headers().containsKey(CommonConstants.HEADER_USER_LOCALE)) {
      requestTemplate.header(
          CommonConstants.HEADER_USER_LOCALE, CurrentThreadContext.getLogContext().getUserLocale());
    }

    // Attach API Key
    if (!requestTemplate.headers().containsKey(CommonConstants.HEADER_API_KEY)) {
      requestTemplate.header(
          CommonConstants.HEADER_API_KEY, CurrentThreadContext.getLogContext().getApiKey());
    }
  }
}
