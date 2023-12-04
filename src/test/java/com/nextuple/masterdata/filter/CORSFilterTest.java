/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
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
