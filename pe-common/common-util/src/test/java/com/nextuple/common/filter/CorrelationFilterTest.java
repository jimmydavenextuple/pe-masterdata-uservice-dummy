package com.nextuple.common.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.LogContext;
import com.nextuple.common.exception.APIKeyHeaderNotFoundException;
import com.nextuple.common.exception.TenantIdHeaderNotFoundException;
import com.nextuple.common.util.CorrelationUtil;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

public class CorrelationFilterTest {

  @DisplayName("Should not throw any exceptions since tenantId and apiKey are both set")
  @Test
  void doFilterTest() throws IOException, ServletException {
    CorrelationFilter correlationFilter = new CorrelationFilter();

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    MockFilterChain mockFilterChain = new MockFilterChain();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";
    String apiKey = "apiKey";
    String tenantId = "tenantId";

    CurrentThreadContext.getLogContext().setApiKey(apiKey);
    CurrentThreadContext.getLogContext().setTenantId(tenantId);
    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(true);
    CorrelationUtil.setServiceCorrelationId(correlationId);

    assertDoesNotThrow(
        () -> {
          correlationFilter.doFilter(
              mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
        });
  }

  @DisplayName(
      "Should attach Correlation ID and Service Correlation ID in response headers since `commited` in the response is set to false")
  @Test
  void doFilterResponseNotCommitted() throws IOException, ServletException {
    CorrelationFilter correlationFilter = new CorrelationFilter();

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    MockFilterChain mockFilterChain = new MockFilterChain();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";
    String applicationVersion = "1.0";
    String apiKey = "apiKey";
    String tenantId = "tenantId";

    ReflectionTestUtils.setField(correlationFilter, "applicationVersion", "1.0");
    CurrentThreadContext.getLogContext().setApiKey(apiKey);
    CurrentThreadContext.getLogContext().setTenantId(tenantId);
    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(false);

    System.out.println(CorrelationUtil.getCurrentServiceCorrelationId());

    correlationFilter.doFilter(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);

    assertEquals(correlationId, mockHttpServletResponse.getHeader(LogContext.CORRELATION_ID));
    assertFalse(mockHttpServletResponse.getHeader(LogContext.SERVICE_CORRELATION_ID).isEmpty());
    assertEquals(applicationVersion, mockHttpServletResponse.getHeader("X-App-Version"));
  }

  @DisplayName("Should throw TenantIdHeaderNotFoundException since tenantId is not set")
  @Test
  void doFilterThrowsTenantIdExceptionTest() throws IOException, ServletException {
    CorrelationFilter correlationFilter = new CorrelationFilter();

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    MockFilterChain mockFilterChain = new MockFilterChain();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";
    String apiKey = "apiKey";

    ReflectionTestUtils.setField(correlationFilter, "failIfTenantIdNotPresent", true);

    CurrentThreadContext.getLogContext().setApiKey(apiKey);
    CurrentThreadContext.getLogContext().setTenantId("");
    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(true);
    CorrelationUtil.setServiceCorrelationId(correlationId);

    assertThrows(
        TenantIdHeaderNotFoundException.class,
        () -> {
          correlationFilter.doFilter(
              mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
        });
  }

  @DisplayName("Should throw APIKeyHeaderNotFoundException since apikey is not set")
  @Test
  void doFilterThrowsApiKeyExceptionTest() throws IOException, ServletException {
    CorrelationFilter correlationFilter = new CorrelationFilter();

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    MockFilterChain mockFilterChain = new MockFilterChain();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";
    String tenantId = "tenantId";

    ReflectionTestUtils.setField(correlationFilter, "failIfApiKeyNotPresent", true);

    CurrentThreadContext.getLogContext().setApiKey("");
    CurrentThreadContext.getLogContext().setTenantId(tenantId);
    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(true);
    CorrelationUtil.setServiceCorrelationId(correlationId);

    assertThrows(
        APIKeyHeaderNotFoundException.class,
        () -> {
          correlationFilter.doFilter(
              mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
        });
  }
}
