/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEMetaDataCacheValue;
import com.nextuple.pe.userexit.spring.cache.feign.UEMetaDataFeignImpl;
import com.nextuple.pe.userexit.spring.cache.mapper.UEMetaDataMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UEMetaDataFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        UEMetaDataCacheKey, UEMetaDataCacheValue, String, BaseResponse<UserExitMetaDataDto>> {

  private final UEMetaDataFeignImpl ueMetaDataFeign;
  private final UEMetaDataMapper ueMetaDataMapper;

  @Override
  public UEMetaDataCacheValue get(UEMetaDataCacheKey key) {
    try {
      BaseResponse<UserExitMetaDataDto> response =
          ueMetaDataFeign.fetchMetaData(key.getAppName(), key.getServiceName(), key.getName());

      if (Objects.isNull(response.getPayload())) {
        return UEMetaDataCacheValue.builder().userExitMetaDataDto(null).build();
      }
      return ueMetaDataMapper.responseToCacheValue(response);
    } catch (RuntimeException ex) {
      return UEMetaDataCacheValue.builder().userExitMetaDataDto(null).build();
    }
  }
}
