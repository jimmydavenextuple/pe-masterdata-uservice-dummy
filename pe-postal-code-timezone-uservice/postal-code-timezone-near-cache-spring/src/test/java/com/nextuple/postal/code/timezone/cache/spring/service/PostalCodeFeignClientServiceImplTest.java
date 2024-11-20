/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.cache.spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.postal.code.timezone.cache.domain.PostalCodeCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.feign.PostalCodeFeignImpl;
import com.nextuple.postal.code.timezone.cache.spring.mapper.PostalCodeMapper;
import com.nextuple.postal.code.timezone.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostalCodeFeignClientServiceImplTest {

  @InjectMocks private PostalCodeFeignClientServiceImpl postalCodeFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock private PostalCodeMapper mapper;

  @Mock private PostalCodeFeignImpl postalCodeFeign;

  @Test
  void getTest() {
    PostalCodeCacheKey cacheKey = testUtil.getPostalCodeCacheKey();
    PostalCodeCacheValue cacheValue = testUtil.getPostalCodeCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(postalCodeFeign.getPostalCode(any(), any()))
        .thenReturn(testUtil.getPostalCodeBaseResponse());

    assertEquals(cacheValue, postalCodeFeignClientService.get(cacheKey));
    assertFalse(postalCodeFeignClientService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    PostalCodeCacheKey invalidCacheKey = testUtil.getPostalCodeCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(postalCodeFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
