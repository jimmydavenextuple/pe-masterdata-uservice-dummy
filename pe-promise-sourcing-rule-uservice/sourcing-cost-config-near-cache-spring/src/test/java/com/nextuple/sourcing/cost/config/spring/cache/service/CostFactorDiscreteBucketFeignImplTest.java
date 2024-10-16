/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorDiscreteBucketCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorDiscreteBucketCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.spring.cache.feign.CostFactorDiscreteBucketFeignImpl;
import com.nextuple.sourcing.cost.config.spring.cache.mapper.CostFactorDiscreteBucketMapper;
import com.nextuple.sourcing.cost.config.spring.cache.utils.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CostFactorDiscreteBucketFeignImplTest {
  @InjectMocks
  private CostFactorDiscreteBucketFeignServiceImpl costFactorDiscreteBucketFeignService;

  @InjectMocks private TestUtil testUtil;
  @Mock CostFactorDiscreteBucketMapper mapper;
  @Mock CostFactorDiscreteBucketFeignImpl costFactorDiscreteBucketFeign;

  @Test
  void get() {
    CostFactorDiscreteBucketCacheKey cacheKey = testUtil.getCostFactorDiscreteBucketCacheKey();
    CostFactorDiscreteBucketCacheValue cacheValue =
        testUtil.getCostFactorDiscreteBucketCacheValue();
    BaseResponse<List<CostFactorDiscreteBucketDto>> response =
        testUtil.getCostFactorDiscreteBucketDtoBaseResponse();
    Mockito.when(
            costFactorDiscreteBucketFeign.getCostFactorDiscreteBucket(Mockito.any(), Mockito.any()))
        .thenReturn(response);
    Assertions.assertEquals(cacheValue, costFactorDiscreteBucketFeignService.get(cacheKey));
    Assertions.assertFalse(costFactorDiscreteBucketFeignService.get(cacheKey).isUndefined());
  }

  @Test
  void getForExceptionTest() {

    Mockito.when(
            costFactorDiscreteBucketFeign.getCostFactorDiscreteBucket(Mockito.any(), Mockito.any()))
        .thenReturn(null);
    CostFactorDiscreteBucketCacheValue cacheValue =
        costFactorDiscreteBucketFeignService.get(testUtil.getCostFactorDiscreteBucketCacheKey());
    assertNotNull(cacheValue);
    assertNull(cacheValue.getValueToBucketMapping());
  }
}
