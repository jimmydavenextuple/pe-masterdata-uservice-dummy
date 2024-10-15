/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostalCodeMapperTest {

  @InjectMocks
  private GenericMapper<
          PostalCodeCacheKey, PostalCodeCacheValue, String, BaseResponse<PostalCodeResponse>>
      genericMapper = new PostalCodeMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new PostalCodeCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    PostalCodeCacheValue postalCodeCacheValue = testUtil.getPostalCodeCacheValue();
    BaseResponse<PostalCodeResponse> response = testUtil.getPostalCodeBaseResponse();

    Assertions.assertEquals(postalCodeCacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    PostalCodeCacheValue cacheValue = testUtil.getPostalCodeCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
