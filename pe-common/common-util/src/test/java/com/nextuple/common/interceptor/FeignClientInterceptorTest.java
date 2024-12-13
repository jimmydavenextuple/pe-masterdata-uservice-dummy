package com.nextuple.common.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.LogContext;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeignClientInterceptorTest {

  @DisplayName(
      "Should set `tenantId`, `correlationId`, `username`, `apiKey` as null using constructor")
  @Test
  void applyNullTest() {
    CurrentThreadContext.getLogContext().setTenantId(null);
    CurrentThreadContext.getLogContext().setCorrelationId(null);
    CurrentThreadContext.getLogContext().setUsername(null);
    CurrentThreadContext.getLogContext().setApiKey(null);
    CurrentThreadContext.getLogContext().setAuthorizationHeader(null);

    FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();
    RequestTemplate requestTemplate = new RequestTemplate();
    feignClientInterceptor.apply(requestTemplate);

    assertFalse(
        requestTemplate.headers().containsKey(CommonConstants.HEADER_TENANT_ID), "TenantId");
    assertFalse(requestTemplate.headers().containsKey(LogContext.CORRELATION_ID), "CorrelationId");
    assertFalse(requestTemplate.headers().containsKey(CommonConstants.HEADER_USER), "Username");
    assertTrue(
        requestTemplate.headers().containsKey(CommonConstants.HEADER_USER_LOCALE), "UserLocale");
    assertFalse(requestTemplate.headers().containsKey(CommonConstants.HEADER_API_KEY), "Api Key");
    assertFalse(
        requestTemplate.headers().containsKey(CommonConstants.AUTHORIZATION_HEADER),
        "Authorization");
  }

  @DisplayName(
      "Should set the tenantId, correlationId, username, apiKey from function arguments and feignClientInterceptor should have the same values.")
  @Test
  void applyTest1() {
    String tenantId = "tenantId";
    String correlationId = "correlationId";
    String username = "username";
    String apiKey = "apiKey";

    CurrentThreadContext.getLogContext().setRequestHeader("authorization", "token");
    CurrentThreadContext.getLogContext().setTenantId(tenantId);
    CurrentThreadContext.getLogContext().setCorrelationId(correlationId);
    CurrentThreadContext.getLogContext().setUsername(username);
    CurrentThreadContext.getLogContext().setApiKey(apiKey);

    FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();
    RequestTemplate requestTemplate = new RequestTemplate();

    feignClientInterceptor.apply(requestTemplate);

    assertTrue(
        requestTemplate.headers().get(CommonConstants.HEADER_TENANT_ID).contains(tenantId),
        "Tenant Id");
    assertTrue(
        requestTemplate.headers().get(LogContext.CORRELATION_ID).contains(correlationId),
        "Correlation Id");
    assertTrue(
        requestTemplate.headers().get(CommonConstants.HEADER_USER).contains(username), "Username");
    assertTrue(
        requestTemplate.headers().get(CommonConstants.HEADER_API_KEY).contains(apiKey), "Api Key");
    assertTrue(
        requestTemplate.headers().get(CommonConstants.AUTHORIZATION_HEADER).contains("token"));
  }

  @Test
  void applyTest2() {

    FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();
    RequestTemplate requestTemplate = new RequestTemplate();
    requestTemplate.header(LogContext.CORRELATION_ID, "CorrelationId");
    requestTemplate.header(CommonConstants.HEADER_USER, "Username");
    requestTemplate.header(CommonConstants.HEADER_TENANT_ID, "TenantId");
    requestTemplate.header(CommonConstants.HEADER_API_KEY, "Api Key");
    requestTemplate.header(CommonConstants.AUTHORIZATION_HEADER, "Authorization");
    requestTemplate.header(CommonConstants.HEADER_USER_LOCALE, "UserLocale");

    feignClientInterceptor.apply(requestTemplate);
    assertTrue(requestTemplate.headers().containsKey(CommonConstants.HEADER_TENANT_ID), "TenantId");
    assertTrue(requestTemplate.headers().containsKey(LogContext.CORRELATION_ID), "CorrelationId");
    assertTrue(requestTemplate.headers().containsKey(CommonConstants.HEADER_USER), "Username");
    assertTrue(
        requestTemplate.headers().containsKey(CommonConstants.HEADER_USER_LOCALE), "UserLocale");
    assertTrue(requestTemplate.headers().containsKey(CommonConstants.HEADER_API_KEY), "Api Key");
    assertTrue(
        requestTemplate.headers().containsKey(CommonConstants.AUTHORIZATION_HEADER),
        "Authorization");
  }
}
