/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.transit.cache.domain.ZoneCacheKey;
import com.nextuple.transit.cache.domain.ZoneCacheValue;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.spring.cache.feign.ZoneFeignImpl;
import com.nextuple.transit.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZoneFeignClientServiceImplTest {

  @InjectMocks private ZoneFeignClientServiceImpl zoneFeignClientServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<ZoneCacheKey, ZoneCacheValue, String, BaseResponse<List<ZoneResponse>>>
      mapper;

  @Mock private ZoneFeignImpl zoneFeign;

  @Test
  void getTest() {
    ZoneCacheKey cacheKey = testUtil.getZoneCacheKey();
    ZoneCacheValue cacheValue = testUtil.getZoneCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(zoneFeign.getZoneDetailsList(any(), any())).thenReturn(testUtil.getBaseResponseOfZone());

    assertEquals(cacheValue, zoneFeignClientServiceImpl.get(cacheKey));
    assertNotNull(zoneFeignClientServiceImpl.get(cacheKey).getZoneMap(TestUtil.ORG_ID));
    assertFalse(zoneFeignClientServiceImpl.get(cacheKey).isUndefined());
    verify(mapper, times(3)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    ZoneCacheKey invalidCacheKey = testUtil.getZoneCacheKey();
    assertNotNull(zoneFeignClientServiceImpl.get(invalidCacheKey));
    verify(mapper, times(0)).responseToCacheValue(any());
  }
}
