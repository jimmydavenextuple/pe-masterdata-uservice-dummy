package com.nextuple.common.interceptor;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.CommonServiceException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

class RestRequestInterceptorTest {
  @InjectMocks RestRequestInterceptor interceptor;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private Object handler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void preHandleTestWithQueryParam() throws Exception {
    handler = new Object();
    request = new MockHttpServletRequest();

    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE");
    Map<String, String> map = new HashMap<>();
    map.put("orgId", "NEXTUPLE");
    Map<String, String[]> queryMap = new HashMap<>();
    queryMap.put("orgId", new String[] {"NEXTUPLE"});
    request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
    request.setParameters(queryMap);
    boolean result = interceptor.preHandle(request, response, handler);
    Assertions.assertTrue(result);
  }

  @Test
  void preHandleTestWithoutQueryParam() throws Exception {
    handler = new Object();
    request = new MockHttpServletRequest();
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE");
    Map<String, String> map = new HashMap<>();
    map.put("orgId", "NEXTUPLE");
    request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
    boolean result = interceptor.preHandle(request, response, handler);
    Assertions.assertTrue(result);
  }

  @Test
  void preHandleTestWithQueryParamWrongTenant() throws Exception {
    handler = new Object();
    request = new MockHttpServletRequest();
    interceptor = new RestRequestInterceptor(true);
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE");
    Map<String, String> map = new HashMap<>();
    map.put("orgId", "NEXTUPLE");
    Map<String, String[]> queryMap = new HashMap<>();
    queryMap.put("orgId", new String[] {"NEXT"});
    request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
    request.setParameters(queryMap);
    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              interceptor.preHandle(request, response, handler);
            });
    Assertions.assertEquals(HttpStatus.FORBIDDEN, cse.getHttpStatus());
  }

  @Test
  void preHandleTestFailureCase() throws Exception {
    handler = new Object();
    request = new MockHttpServletRequest();
    interceptor = new RestRequestInterceptor(true);
    CurrentThreadContext.getLogContext().setTenantId("NXTPLE");
    Map<String, String> map = new HashMap<>();
    map.put("orgId", "NEXTUPLE");
    request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
    CommonServiceException cse =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              interceptor.preHandle(request, response, handler);
            });
    Assertions.assertEquals(HttpStatus.FORBIDDEN, cse.getHttpStatus());
  }

  @Test
  void preHandleTestWithAllowedUrl() throws Exception {
    handler = new Object();
    request = new MockHttpServletRequest();
    request.setRequestURI("/node/get-all-cache-keys");

    boolean result = interceptor.preHandle(request, response, handler);
    Assertions.assertTrue(result);
  }

  @Test
  void preHandleTestWithOptionsRequest() throws Exception {
    handler = new Object();
    request = new MockHttpServletRequest();
    request.setMethod("OPTIONS");
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE");
    Map<String, String> map = new HashMap<>();
    map.put("orgId", "NEXTUPLE");
    request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
    boolean result = interceptor.preHandle(request, response, handler);
    Assertions.assertFalse(result);
  }
}
