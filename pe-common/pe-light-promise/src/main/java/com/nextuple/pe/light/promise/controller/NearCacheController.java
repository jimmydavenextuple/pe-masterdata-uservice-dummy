/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.light.promise.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.light.promise.service.NearCacheService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for evicting near caches
 *
 * <p>This controller provides API to completely evict near cache.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Hidden
public class NearCacheController {

  private final NearCacheService nearCacheService;

  @Operation(summary = "Evict near cache", description = "Deletes all the values in near cache.")
  @DeleteMapping(value = "/evict-cache")
  public ResponseEntity<BaseResponse<String>> evictNearCache() {
    try {
      nearCacheService.deleteAllNearCacheData();
      return ResponseEntity.ok(
          BaseResponse.builder().message("Near cache values evicted successfully!").build());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              BaseResponse.builder()
                  .message("Error in performing near cache eviction Reason: " + e.getMessage())
                  .build());
    }
  }
}
