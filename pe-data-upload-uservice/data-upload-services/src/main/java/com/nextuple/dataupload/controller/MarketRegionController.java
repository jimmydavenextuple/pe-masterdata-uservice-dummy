/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.GetMarketRegionsDoc;
import com.nextuple.dataupload.service.MarketRegionService;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for market regions APIs.
 *
 * <p>This controller provides APIs to fetch market regions for organizations based on the specified
 * organization ID.
 *
 * <p>The controller is tagged with "Market Regions APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/ui/regions-nodes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Market Regions APIs")
public class MarketRegionController {
  private final MarketRegionService marketRegionService;

  /**
   * Retrieves a list of market regions for the specified organization.
   *
   * <p>This method processes a GET request to fetch market regions based on the provided
   * organization ID. It returns a list of market region details associated with the organization.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of market
   *     regions.
   */
  @GetMapping("/market-regions/orgId/{orgId}")
  @GetMarketRegionsDoc
  public ResponseEntity<BaseResponse<List<MarketRegionInfo>>> getMarketRegions(
      @NotBlank(message = "OrgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId) {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Market Regions fetched successfully")
            .payload(marketRegionService.getMarketRegions(orgId))
            .build());
  }
}
