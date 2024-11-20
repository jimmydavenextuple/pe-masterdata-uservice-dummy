/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.sourcing.cost.config.cache.domain.PreferenceSelectorCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.PreferenceSelectorCacheValue;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreferenceSelectorMapperTest {

  @InjectMocks
  private GenericMapper<
          PreferenceSelectorCacheKey,
          PreferenceSelectorCacheValue,
          String,
          BaseResponse<PreferenceSelectorDto>>
      genericMapper = new PreferenceSelectorMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new PreferenceSelectorCacheKey()));
  }

  @Test
  void responseToCacheValue() {

    Assertions.assertEquals(
        testUtil.getPreferenceSelectorCacheValue(),
        genericMapper.responseToCacheValue(testUtil.getPreferenceSelectorDto()));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(genericMapper.cacheValueToResponse(testUtil.getPreferenceSelectorCacheValue()));
  }
}
