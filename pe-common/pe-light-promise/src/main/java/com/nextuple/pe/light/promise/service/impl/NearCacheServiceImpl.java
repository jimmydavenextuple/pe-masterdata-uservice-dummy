/*

* Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.

*

* The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.

* The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.

*/

package com.nextuple.pe.light.promise.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.pe.light.promise.service.NearCacheService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NearCacheServiceImpl implements NearCacheService {

  private final List<GenericNearCacheService> nearCacheServices; // NOSONAR

  final String NEAR_CACHE_EVICT_ERROR_MESSAGE = "Error while deleting near cache data";

  public void deleteAllNearCacheData() throws CommonServiceException {

    for (GenericNearCacheService genericNearCacheService : nearCacheServices) { // NOSONAR

      try {

        genericNearCacheService.deleteAll();

      } catch (Exception e) {

        log.error(NEAR_CACHE_EVICT_ERROR_MESSAGE, e);

        throw new CommonServiceException(
            NEAR_CACHE_EVICT_ERROR_MESSAGE,
            e,
            HttpStatus.INTERNAL_SERVER_ERROR,
            0x2003,
            Map.of(
                "service",
                FieldError.builder()
                    .rejectedValue(genericNearCacheService.getClass().getSimpleName())
                    .build()));
      }
    }
  }
}
