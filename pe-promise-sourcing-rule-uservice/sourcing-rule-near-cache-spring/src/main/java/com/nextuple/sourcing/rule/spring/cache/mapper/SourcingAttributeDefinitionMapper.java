/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.rule.spring.cache.mapper;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingAttributeDefinitionByActiveStatusValue;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class SourcingAttributeDefinitionMapper
    implements GenericMapper<
        SourcingAttributeDefinitionByActiveStatusKey,
        SourcingAttributeDefinitionByActiveStatusValue,
        String,
        BaseResponse<SourcingAttributesDefinitionResponse>> {
  public static final DataMapper DATA_MAPPER = Mappers.getMapper(DataMapper.class);

  @Override
  public SourcingAttributeDefinitionByActiveStatusKey requestToCacheKey(String request) {
    return null;
  }

  @Override
  public String cacheKeyToRequest(SourcingAttributeDefinitionByActiveStatusKey cacheKey) {
    return null;
  }

  @Override
  public SourcingAttributeDefinitionByActiveStatusValue responseToCacheValue(
      BaseResponse<SourcingAttributesDefinitionResponse> resp) {
    return DATA_MAPPER.toSourcingAttributeDefinitionByActiveStatus(resp.getPayload());
  }

  @Override
  public BaseResponse<SourcingAttributesDefinitionResponse> cacheValueToResponse(
      SourcingAttributeDefinitionByActiveStatusValue cacheValue) {
    return null;
  }
}
