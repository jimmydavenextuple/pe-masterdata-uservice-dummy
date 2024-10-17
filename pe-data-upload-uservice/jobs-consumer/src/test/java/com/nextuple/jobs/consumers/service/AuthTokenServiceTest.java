/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.feign.AuthTokenAPI;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

  @InjectMocks AuthTokenService authTokenService;

  @InjectMocks TestUtil testUtil;

  @Mock AuthTokenAPI authTokenAPI;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(authTokenService, "grantType", "client_credentials");
    ReflectionTestUtils.setField(authTokenService, "scope", "sfcc-resources/edd");
  }

  @Test
  void getAuthTokenTest() {
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    String response = authTokenService.getAuthToken("dummy", "1996-09-05T00:00:00");
    Assertions.assertNotNull(response);
  }

  @Test
  void getAuthTokenTest2() {
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    String response = authTokenService.getAuthToken("dummy", "");
    Assertions.assertNotNull(response);
  }

  @Test
  void getAuthTokenTest3() {
    var dateTime = LocalDateTime.now();
    dateTime = dateTime.plusDays(1);
    String response = authTokenService.getAuthToken("dummy", dateTime.toString());
    Assertions.assertNotNull(response);
  }
}
