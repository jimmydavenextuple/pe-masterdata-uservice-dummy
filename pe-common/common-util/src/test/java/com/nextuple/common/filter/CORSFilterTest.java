package com.nextuple.common.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

class CORSFilterTest {
  @Mock private Environment environment;
  @Mock private FilterChain filterChain;
  @Mock private HttpServletResponse httpServletResponse;
  @Mock private ServletResponse servletResponse;
  @Mock private ServletRequest servletRequest;
  @InjectMocks private CORSFilter corsFilter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        corsFilter, "allowedEnvironments", new String[] {"dev", "qa", "default"});
  }

  @Test
  void testDoFilterForAllowedEnv() throws ServletException, IOException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"dev"});
    when(servletResponse.isCommitted()).thenReturn(false);

    corsFilter.doFilter(servletRequest, httpServletResponse, filterChain);

    verify(httpServletResponse).setHeader("Access-Control-Allow-Origin", "*");
    verify(httpServletResponse).setHeader("Access-Control-Allow-Headers", "*");
    verify(httpServletResponse).setHeader("Access-Control-Allow-Methods", "*");
    verify(filterChain).doFilter(any(), any());
  }

  @Test
  void testDoFilterForNonAllowedEnv() throws ServletException, IOException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"prod"});
    when(servletResponse.isCommitted()).thenReturn(false);

    corsFilter.doFilter(servletRequest, httpServletResponse, filterChain);

    verify(httpServletResponse, never()).setHeader(eq("Access-Control-Allow-Origin"), anyString());
    verify(httpServletResponse, never()).setHeader(eq("Access-Control-Allow-Headers"), anyString());
    verify(httpServletResponse, never()).setHeader(eq("Access-Control-Allow-Methods"), anyString());
    verify(filterChain).doFilter(any(), any());
  }

  @Test
  void testDoFilterForResponseCommitted() throws ServletException, IOException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"dev"});
    when(servletResponse.isCommitted()).thenReturn(true);

    corsFilter.doFilter(servletRequest, servletResponse, filterChain);

    verifyNoInteractions(httpServletResponse);
    verify(filterChain).doFilter(any(), any());
  }
}
