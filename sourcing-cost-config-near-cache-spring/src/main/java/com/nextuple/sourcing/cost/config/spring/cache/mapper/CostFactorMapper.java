/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class CostFactorMapper
    implements GenericMapper<
        CostFactorCacheKey, CostFactorCacheValue, String, BaseResponse<CostFactorDto>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public CostFactorCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(CostFactorCacheKey cacheKey) {
    return null;
  }

  @Override
  public CostFactorCacheValue responseToCacheValue(BaseResponse<CostFactorDto> resp) {
    return DATA_MAPPER.toCostFactorCacheValue(resp.getPayload());
  }

  @Override
  public BaseResponse<CostFactorDto> cacheValueToResponse(CostFactorCacheValue cacheValue) {
    return null;
  }
}
