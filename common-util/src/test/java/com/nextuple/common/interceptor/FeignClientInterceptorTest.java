package com.nextuple.common.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

class FeignClientInterceptorTest {

  @Test
  void applyTest() {
    String authorization = "authorization";

    CurrentThreadContext.getLogContext().setAuthorizationHeader(authorization);

    FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();
    RequestTemplate requestTemplate = new RequestTemplate();

    feignClientInterceptor.apply(requestTemplate);
    assertTrue(
        requestTemplate.headers().get(CommonConstants.AUTHORIZATION_HEADER).contains(authorization),
        "authorization");
  }

  @Test
  void applyNullTest() {
    CurrentThreadContext.getLogContext().setAuthorizationHeader(null);

    FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();
    RequestTemplate requestTemplate = new RequestTemplate();

    feignClientInterceptor.apply(requestTemplate);
    assertFalse(
        requestTemplate.headers().containsKey(CommonConstants.AUTHORIZATION_HEADER),
        "Authorization");
  }
}
