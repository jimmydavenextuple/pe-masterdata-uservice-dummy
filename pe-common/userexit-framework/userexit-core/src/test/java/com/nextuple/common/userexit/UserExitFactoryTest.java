/*
 *
 *  * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.common.userexit;

import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserExitFactoryTest {

  @InjectMocks UserExitFactory<String, String> userExitFactory;
  @Mock RegularUEImpl<String, String> regularUE;
  @Mock ApiUEImpl<String, String> apiUE;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Return regular UE")
  void regularUEFactoryTest() {
    IUserExit<String, String> userExit = userExitFactory.getUE(UserExitTypeEnum.REGULAR);
    Assertions.assertNotNull(userExit);
    Assertions.assertTrue(userExit instanceof RegularUEImpl);
  }

  @Test
  @DisplayName("Return API UE")
  void ApiUEFactoryTest() {
    IUserExit<String, String> userExit = userExitFactory.getUE(UserExitTypeEnum.API);
    Assertions.assertNotNull(userExit);
    Assertions.assertTrue(userExit instanceof ApiUEImpl);
  }

  @Test
  @DisplayName("Return null for invalid UE")
  void UEFactoryNullTest() {
    IUserExit<String, String> userExit = userExitFactory.getUE(null);
    Assertions.assertNull(userExit);
  }
}
