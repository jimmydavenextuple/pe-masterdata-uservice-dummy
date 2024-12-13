/*
 *
 *  * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.common.userexit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.ErrorWrapper;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.util.TestUtil;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class RegularUEImplTest {
  @InjectMocks RegularUEImpl<String, String> regularUE;
  @InjectMocks TestUtil testUtil;
  @Mock HttpUtil<String, String> httpUtil;
  private static final MockClock clock = new MockClock();
  private static final SimpleMeterRegistry meterRegistry =
      new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(regularUE, "meterRegistry", meterRegistry);
  }

  @Test
  @DisplayName("Invoke UE - Call URL")
  void invokeRegularUETest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    when(httpUtil.makePOSTCall(any(), any(), any(), any()))
        .thenReturn(ErrorWrapper.<String>builder().data("Modified Output").build());
    ErrorWrapper<String> response = regularUE.invoke("InputData", null, userExitData, null, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals("Modified Output", response.getData());
    verify(httpUtil, times(1)).makePOSTCall(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke UE - Mock Call")
  void invokeMockTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitConfigData().setUeImplType(UEImplTypeEnum.CLASS_BASED);
    ErrorWrapper<String> response = regularUE.invoke("Input", null, userExitData, null, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals("Input", response.getData());

    verify(httpUtil, times(0)).makePOSTCall(any(), any(), any(), any());
  }
}
