/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.feign;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Encoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class FeignClientConfigurationTest {

  @InjectMocks AuthTokenApiFeignClientConfiguration authTokenApiFeignClientConfiguration;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(authTokenApiFeignClientConfiguration, "username", "user");
    ReflectionTestUtils.setField(authTokenApiFeignClientConfiguration, "password", "password");
  }

  @Test
  void basicAuthRequestInterceptorTest() {
    BasicAuthRequestInterceptor basicAuthRequestInterceptor =
        authTokenApiFeignClientConfiguration.basicAuthRequestInterceptor();

    assertNotNull(basicAuthRequestInterceptor);
  }

  @Test
  void encoder() {
    Encoder encoder = authTokenApiFeignClientConfiguration.encoder();
    Assertions.assertNotNull(encoder);
  }
}
