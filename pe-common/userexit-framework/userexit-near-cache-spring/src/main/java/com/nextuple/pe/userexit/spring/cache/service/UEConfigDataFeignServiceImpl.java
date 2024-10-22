/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheKey;
import com.nextuple.pe.userexit.cache.domain.UEConfigDataCacheValue;
import com.nextuple.pe.userexit.spring.cache.feign.UEConfigDataFeignImpl;
import com.nextuple.pe.userexit.spring.cache.mapper.UEConfigDataMapper;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
// Added this
@AllArgsConstructor
public class UEConfigDataFeignServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        UEConfigDataCacheKey, UEConfigDataCacheValue, String, BaseResponse<UserExitConfigDataDto>> {

  @Autowired UEConfigDataFeignImpl ueConfigDataFeign;
  @Autowired UEConfigDataMapper ueConfigDataMapper;

  @Override
  public UEConfigDataCacheValue get(UEConfigDataCacheKey key) {
    try {
      BaseResponse<UserExitConfigDataDto> response =
          ueConfigDataFeign.fetchConfigData(
              key.getOrgId(), key.getAppName(), key.getServiceName(), key.getUserExitName());

      if (Objects.isNull(response.getPayload())) {
        return UEConfigDataCacheValue.builder().userExitConfigDataDto(null).build();
      }
      return ueConfigDataMapper.responseToCacheValue(response);
    } catch (RuntimeException ex) {
      return UEConfigDataCacheValue.builder().userExitConfigDataDto(null).build();
    }
  }
}
