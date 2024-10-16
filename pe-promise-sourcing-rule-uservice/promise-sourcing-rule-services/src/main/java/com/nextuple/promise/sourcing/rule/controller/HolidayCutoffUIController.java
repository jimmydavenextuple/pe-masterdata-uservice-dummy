/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.service.HolidayCutoffService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/holiday-cutoff/v1")
@RequiredArgsConstructor
@Tag(name = "Holiday cutoff Dashboard APIs")
@Slf4j
public class HolidayCutoffUIController {
  private final HolidayCutoffService holidayCutoffService;

  @PostMapping(value = "/orgId/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> getHolidayCutoffDetails(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId,
      @RequestParam(required = false, defaultValue = "true") Boolean isPaginated,
      @RequestParam(required = false, defaultValue = "1") Integer pageNo,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "ruleName") String sortBy,
      @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
      @Valid @RequestBody HolidayCutoffUIRequest holidayCutoffUIRequest)
      throws CommonServiceException, PromiseEngineException {
    log.debug("Processing get holiday cutoff details for UI");

    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(pageNo));
    pageParams.setPageSize(Optional.of(pageSize));
    pageParams.setSortBy(Optional.of(sortBy));
    pageParams.setSortOrder(Optional.of(sortOrder));

    PageResponseForHolidayCutoff holidayCutoffDetails =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            orgId, isPaginated, pageParams, holidayCutoffUIRequest);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff details fetched successfully")
            .payload(holidayCutoffDetails)
            .build());
  }
}
