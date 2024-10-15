/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheValue;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransitBufferV2MapperTest {

  @InjectMocks private TransitBufferV2Mapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getTransitBufferV2CacheKey()));
  }

  @Test
  void responseToCacheValue() {
    TransitBufferV2CacheValue transitBufferV2CacheValue = testUtil.getTransitBufferV2CacheValue();

    BaseResponse<List<TransitBufferDetailsResponse>> response =
        testUtil.getBaseResponseOfTransitBuffer();
    var responseCacheValue =
        mapper.responseToCacheValue(response).getTransitBufferDetailsResponseList();
    assertEquals(
        transitBufferV2CacheValue.getTransitBufferDetailsResponseList().size(),
        responseCacheValue.size());
    assertNotNull(responseCacheValue.get(0));
    assertEquals(
        transitBufferV2CacheValue
            .getTransitBufferDetailsResponseList()
            .get(0)
            .getCarrierServiceId(),
        responseCacheValue.get(0).getCarrierServiceId());
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getTransitBufferV2CacheValue()));
  }
}
