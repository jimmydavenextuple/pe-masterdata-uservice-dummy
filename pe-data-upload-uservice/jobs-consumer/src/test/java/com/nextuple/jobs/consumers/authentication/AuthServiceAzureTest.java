/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.authentication;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@ExtendWith(MockitoExtension.class)
class AuthServiceAzureTest {
  @InjectMocks private AuthServiceAzure authServiceAzure;

  @InjectMocks private TestUtil testUtil;

  @Test
  void checkAuthExpiry() {
    authServiceAzure.checkAuthExpiry(null);
    Assertions.assertNotNull(authServiceAzure);
  }

  @Test
  void setAuthTokenInThreadContextTest() {
    authServiceAzure.setAuthTokenInThreadContext();
    Assertions.assertNotNull(authServiceAzure);
  }

  @Test
  void testJobServiceMessage() {
    Message<RecordDto> headerWithOutAuth =
        MessageBuilder.withPayload(testUtil.getRecordDto(JobTypeEnum.TRANSIT_BUFFER_REQUEST))
            .build();
    Message<RecordDto> message = authServiceAzure.setAuthHeaders(headerWithOutAuth);
    Assertions.assertNotNull(message);
  }
}
