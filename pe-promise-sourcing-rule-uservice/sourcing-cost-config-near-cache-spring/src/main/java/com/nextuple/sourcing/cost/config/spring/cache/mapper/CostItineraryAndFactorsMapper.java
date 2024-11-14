/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class CostItineraryAndFactorsMapper
    implements GenericMapper<
        CostItineraryAndFactorsCacheKey,
        CostItineraryAndFactorsCacheValue,
        String,
        BaseResponse<CostItineraryAndFactorsDto>> {

  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public CostItineraryAndFactorsCacheKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(CostItineraryAndFactorsCacheKey cacheKey) {
    return null;
  }

  @Override
  public CostItineraryAndFactorsCacheValue responseToCacheValue(
      BaseResponse<CostItineraryAndFactorsDto> resp) {
    return DATA_MAPPER.toCostItineraryAndFactorsCacheValue(resp.getPayload());
  }

  @Override
  public BaseResponse<CostItineraryAndFactorsDto> cacheValueToResponse(
      CostItineraryAndFactorsCacheValue cacheValue) {
    return null;
  }
}
