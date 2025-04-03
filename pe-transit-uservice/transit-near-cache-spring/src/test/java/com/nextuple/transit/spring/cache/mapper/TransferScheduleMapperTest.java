/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.cache.domain.TransferScheduleCacheValue;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferScheduleMapperTest {

  @InjectMocks private TransferScheduleMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getTransferScheduleCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    TransferScheduleCacheValue transferScheduleCacheValue =
        testUtil.getTransferScheduleCacheValue();

    BaseResponse<List<TransferScheduleRangeResponse>> response =
        testUtil.getListBaseResponseOfTransferScheduleResponse();
    var responseCacheValue =
        mapper.responseToCacheValue(response).getTransferScheduleResponseList();
    assertEquals(
        transferScheduleCacheValue.getTransferScheduleResponseList().size(),
        responseCacheValue.size());
    assertNotNull(responseCacheValue.getFirst());
    assertEquals(
        transferScheduleCacheValue.getTransferScheduleResponseList().getFirst().getSourceNodeId(),
        responseCacheValue.getFirst().getSourceNodeId());
    assertEquals(
        transferScheduleCacheValue.getTransferScheduleResponseList().getFirst().getDropoffNodeId(),
        responseCacheValue.getFirst().getDropoffNodeId());
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getTransferScheduleCacheValue()));
  }
}
