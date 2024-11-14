/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.GetMarketRegionsDoc;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/custom-region")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Custom Regions APIs")
public class CustomRegionDashboardController {

  private final PostalCodeFeign postalCodeFeign;

  private final PageProperties pageProperties;

  private static final String DEFAULT_SORT_BY_CUSTOM_REGION = "customRegionId";

  @GetMapping("/orgId/{orgId}/country/{country}")
  @GetMarketRegionsDoc
  public ResponseEntity<BaseResponse<PagePayload<CustomRegionInfo>>> getCustomRegions(
      @NotBlank(message = "OrgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotBlank(message = "Country can't be empty")
          @PathVariable
          @Parameter(description = "Identifier for Country.")
          String country,
      @RequestParam(required = false)
          @Parameter(description = "Comma Separated values for custom regionID(s)")
          String regionIds,
      @RequestParam(required = false)
          @Parameter(description = "Comma Separated values for custom regionID(s)")
          String regionNames,
      PageParams pageParams) {
    Integer pageNo = pageParams.getPageNo().orElse(pageProperties.getPageNo());
    Integer pageSize = pageParams.getPageSize().orElse(pageProperties.getPageSize());
    String sortBy = pageParams.getSortBy().orElse(DEFAULT_SORT_BY_CUSTOM_REGION);
    String sortOrder = pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER);
    BaseResponse<PagePayload<CustomRegionInfo>> feignResponse =
        postalCodeFeign.getCustomRegionInfo(
            orgId, country, regionIds, regionNames, pageNo, pageSize, sortBy, sortOrder);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message(feignResponse.getMessage())
            .payload(feignResponse.getPayload())
            .build());
  }
}
