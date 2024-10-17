/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.HolidayCutoffDashboardDoc;
import com.nextuple.dataupload.service.HolidayCutoffDetailsService;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holiday-cutoff/v1/ui")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Holiday cutoff UI APIs")
public class HolidayCutoffDashboardController {
  private final Logger logger = LoggerFactory.getLogger(HolidayCutoffDashboardController.class);
  private final HolidayCutoffDetailsService holidayCutoffDetailsService;

  @PostMapping("/orgId/{orgId}")
  @HolidayCutoffDashboardDoc
  public ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> getHolidayCutoffDetails(
      @PathVariable @NotBlank @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      PageParams pageParams,
      @RequestParam(required = false, defaultValue = "true") Boolean isPaginated,
      @Valid @RequestBody HolidayCutoffUIRequest holidayCutoffUIRequest) {
    logger.debug("Processing get holiday cutoff details");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff details List fetched successfully")
            .payload(
                holidayCutoffDetailsService.getHolidayCutoffDetails(
                    orgId, pageParams, isPaginated, holidayCutoffUIRequest))
            .build());
  }
}
