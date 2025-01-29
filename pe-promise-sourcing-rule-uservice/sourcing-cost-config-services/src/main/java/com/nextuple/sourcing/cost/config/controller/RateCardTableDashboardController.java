/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.RateCardTableDashboardDoc;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.service.RateCardTableDashboardService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing rate card table data for a specific organization.
 *
 * <p>This controller handles the process of fetching rate card table data based on an
 * organization's identifier and a provided cost definition request. The data is retrieved by
 * sending a POST request with the relevant details.
 *
 * <p>The controller is tagged with "Rate Card Dashboard APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/cost-config/rate-card")
@RequiredArgsConstructor
@Tag(name = "Rate Card Dashboard APIs")
@Slf4j
public class RateCardTableDashboardController {

  private final RateCardTableDashboardService rateCardTableDashboardService;

  /**
   * Retrieves the rate card table data for the specified organization.
   *
   * <p>This method processes a POST request to fetch rate card table data based on the provided
   * organization identifier and cost definition request.
   *
   * @param orgId The unique identifier of the organization (e.g., "NEXTUPLE_GR").
   * @param costDefinitionRequest The request payload containing the details for fetching the rate
   *     card table data.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched rate card
   *     table data.
   * @throws CommonServiceException If there is an error during the process of fetching rate card
   *     table data.
   */
  @Hidden
  @RateCardTableDashboardDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostDefinitionResponse>> getRateCardTableData(
      @PathVariable String orgId, @RequestBody CostDefinitionRequest costDefinitionRequest)
      throws CommonServiceException {
    log.debug("Processing get rate card table for orgId {}", orgId);
    var costDefinitionResponse =
        rateCardTableDashboardService.getRateCardTableData(orgId, costDefinitionRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost config fetched successfully!")
                .payload(costDefinitionResponse)
                .build());
  }
}
