package com.nextuple.masterdata.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;

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
  }

  @Test
  void testDoFilterForDevEnv() throws ServletException, IOException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"dev"});

    corsFilter.doFilter(servletRequest, httpServletResponse, filterChain);

    verify(httpServletResponse).setHeader("Access-Control-Allow-Origin", "*");
    verify(httpServletResponse).setHeader("Access-Control-Allow-Headers", "*");
    verify(httpServletResponse).setHeader("Access-Control-Allow-Methods", "*");
    verify(filterChain).doFilter(any(), any());
  }

  @Test
  void testDoFilterForQaEnv() throws ServletException, IOException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"qa"});

    corsFilter.doFilter(servletRequest, httpServletResponse, filterChain);

    verify(httpServletResponse).setHeader("Access-Control-Allow-Origin", "*");
    verify(httpServletResponse).setHeader("Access-Control-Allow-Headers", "*");
    verify(httpServletResponse).setHeader("Access-Control-Allow-Methods", "*");
    verify(filterChain).doFilter(any(), any());
  }

  @Test
  void testDoFilterForNonDevQaEnv() throws ServletException, IOException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"perf"});

    corsFilter.doFilter(servletRequest, httpServletResponse, filterChain);

    verifyZeroInteractions(httpServletResponse);
    verify(filterChain).doFilter(any(), any());
  }

  @Test
  void testDoFilterWithNonHttpServletResponse() throws IOException, ServletException {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"qa"});

    corsFilter.doFilter(servletRequest, servletResponse, filterChain);

    verifyZeroInteractions(httpServletResponse);
    verify(filterChain).doFilter(any(), any());
  }
}
