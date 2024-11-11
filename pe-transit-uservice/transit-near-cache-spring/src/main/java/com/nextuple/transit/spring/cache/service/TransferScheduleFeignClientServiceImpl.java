/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.transit.cache.domain.TransferScheduleCacheKey;
import com.nextuple.transit.cache.domain.TransferScheduleCacheValue;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.spring.cache.feign.TransferScheduleFeignImpl;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
// Added this
@RequiredArgsConstructor
public class TransferScheduleFeignClientServiceImpl
    extends AbstractGenericFeignClientServiceImpl<
        TransferScheduleCacheKey,
        TransferScheduleCacheValue,
        String,
        BaseResponse<List<TransferScheduleResponse>>> {
  private final TransferScheduleFeignImpl transferScheduleFeign;

  private final GenericMapper<
          TransferScheduleCacheKey,
          TransferScheduleCacheValue,
          String,
          BaseResponse<List<TransferScheduleResponse>>>
      transferScheduleMapper;

  @Override
  public TransferScheduleCacheValue get(TransferScheduleCacheKey key) {
    try {
      BaseResponse<List<TransferScheduleResponse>> response =
          transferScheduleFeign.fetchTransferSchedules(key.getOrgId(), key.getDropoffNode());
      if (Objects.isNull(response.getPayload()) || response.getPayload().isEmpty()) {
        return TransferScheduleCacheValue.builder().build();
      }
      return transferScheduleMapper.responseToCacheValue(response);
    } catch (RuntimeException ex) {
      return TransferScheduleCacheValue.builder().build();
    }
  }
}
