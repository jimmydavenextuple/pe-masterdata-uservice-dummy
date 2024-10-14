/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffKey;
import com.nextuple.sourcing.rule.cache.domain.HolidayCutoffValue;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class HolidayCutoffMapper
    implements GenericMapper<
        HolidayCutoffKey, HolidayCutoffValue, String, BaseResponse<HolidayCutoffRulesResponse>> {
  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public HolidayCutoffKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(HolidayCutoffKey cacheKey) {
    return null;
  }

  @Override
  public HolidayCutoffValue responseToCacheValue(BaseResponse<HolidayCutoffRulesResponse> resp) {
    return DATA_MAPPER.toHolidayCutoffValue(resp.getPayload());
  }

  @Override
  public BaseResponse<HolidayCutoffRulesResponse> cacheValueToResponse(
      HolidayCutoffValue cacheValue) {
    return null;
  }
}
