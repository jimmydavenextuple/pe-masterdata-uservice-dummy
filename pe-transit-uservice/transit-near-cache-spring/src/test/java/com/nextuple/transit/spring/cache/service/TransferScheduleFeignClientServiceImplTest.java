/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.transit.cache.domain.TransferScheduleCacheKey;
import com.nextuple.transit.cache.domain.TransferScheduleCacheValue;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.spring.cache.feign.TransferScheduleFeignImpl;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferScheduleFeignClientServiceImplTest {

  @InjectMocks private TransferScheduleFeignClientServiceImpl transferScheduleFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          TransferScheduleCacheKey,
          TransferScheduleCacheValue,
          String,
          BaseResponse<List<TransitBufferDetailsResponse>>>
      mapper;

  @Mock private TransferScheduleFeignImpl transferScheduleFeign;

  @Test
  void getTest() {
    TransferScheduleCacheKey cacheKey = testUtil.getTransferScheduleCacheKey();
    TransferScheduleCacheValue cacheValue = testUtil.getTransferScheduleCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(transferScheduleFeign.fetchTransferSchedules(any(), any()))
        .thenReturn(testUtil.getListBaseResponseOfTransferScheduleResponse());

    assertEquals(cacheValue, transferScheduleFeignClientService.get(cacheKey));
    assertNotNull(transferScheduleFeignClientService.get(cacheKey));
    assertFalse(transferScheduleFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(3)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    TransferScheduleCacheKey invalidCacheKey = testUtil.getTransferScheduleCacheKey();
    assertNotNull(transferScheduleFeignClientService.get(invalidCacheKey));
    verify(mapper, times(0)).responseToCacheValue(any());
  }
}
