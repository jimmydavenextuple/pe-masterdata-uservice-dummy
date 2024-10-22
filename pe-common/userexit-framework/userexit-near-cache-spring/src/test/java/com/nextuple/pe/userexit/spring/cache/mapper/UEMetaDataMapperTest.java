/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheValue;
import com.nextuple.pe.userexit.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UEMetaDataMapperTest {

  @InjectMocks
  private GenericMapper<
          UEMetaDataCacheKey, UEMetaDataCacheValue, String, BaseResponse<UserExitMetaDataDto>>
      genericMapper = new UEMetaDataMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new UEMetaDataCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    UEMetaDataCacheValue ueMetaDataCacheValue = testUtil.getUEMetaDataCacheValue();
    BaseResponse<UserExitMetaDataDto> response = testUtil.getUEMetaDataBaseResponse();

    Assertions.assertEquals(ueMetaDataCacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    UEMetaDataCacheValue cacheValue = testUtil.getUEMetaDataCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
