package com.hbc.common.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import com.hbc.common.util.CorrelationUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CORSFilterTest {

  @InjectMocks CORSFilter corsFilter;

  @Test
  void doFilterSuccessTest() throws ServletException, IOException {
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    MockFilterChain mockFilterChain = new MockFilterChain();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";

    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(false);
    CorrelationUtil.setServiceCorrelationId(correlationId);
    String[] profiles = {"default"};

    MockEnvironment environment = new MockEnvironment();

    environment.setActiveProfiles(profiles);

    ReflectionTestUtils.setField(corsFilter, "environment", environment);
    ReflectionTestUtils.setField(
        corsFilter, "corsDisabledEnv", List.of("default", "dev", "qa", "perf", "stage"));

    corsFilter.doFilter(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);

    assertEquals("*", mockHttpServletResponse.getHeader("Access-Control-Allow-Origin"));
    assertEquals("*", mockHttpServletResponse.getHeader("Access-Control-Allow-Headers"));
    assertEquals("*", mockHttpServletResponse.getHeader("Access-Control-Allow-Methods"));
  }

  @Test
  void doFilterCommittedIsTrueTest() throws ServletException, IOException {
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    MockFilterChain mockFilterChain = new MockFilterChain();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";

    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(true);

    corsFilter.doFilter(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);

    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Origin"));
    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Headers"));
    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Methods"));
  }

  @Test
  void doFilterNullEnvTest() {
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";

    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(false);
    CorrelationUtil.setServiceCorrelationId(correlationId);

    ReflectionTestUtils.setField(corsFilter, "environment", null);

    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Origin"));
    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Headers"));
    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Methods"));
  }

  @Test
  void doFilterCROSNotDisabledEnvTest() {
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

    String headerKey = "headerKey";
    String headerValue = "headerValue";
    String correlationId = "correlationId";

    mockHttpServletRequest.addHeader(headerKey, headerValue);
    mockHttpServletRequest.addHeader(correlationId, correlationId);
    mockHttpServletResponse.setCommitted(false);
    CorrelationUtil.setServiceCorrelationId(correlationId);
    String[] profiles = {"prod"};

    MockEnvironment environment = new MockEnvironment();

    environment.setActiveProfiles(profiles);

    ReflectionTestUtils.setField(corsFilter, "environment", null);

    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Origin"));
    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Headers"));
    assertNull(mockHttpServletResponse.getHeader("Access-Control-Allow-Methods"));
  }

  @Test
  void init() {
    MockFilterConfig mockFilterConfig = new MockFilterConfig();

    assertDoesNotThrow(() -> corsFilter.init(mockFilterConfig));
  }

  @Test
  void destroy() {
    assertDoesNotThrow(() -> corsFilter.destroy());
  }
}
