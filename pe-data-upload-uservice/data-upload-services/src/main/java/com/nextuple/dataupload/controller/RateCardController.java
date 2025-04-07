/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.controller.docs.RateCardDoc;
import com.nextuple.dataupload.service.RateCardService;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Rate Card Dashboard APIs.
 *
 * <p>This controller provides an API to interact with the rate card dashboard for managing cost
 * configurations. It allows fetching rate card table data for a specified organization using the
 * provided cost definition request.
 *
 * <p>The controller is tagged with "Rate Card Dashboard APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/cost-config/ui/rate-card")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rate Card Dashboard APIs")
public class RateCardController {

  private final RateCardService rateCardService;

  /**
   * Retrieves rate card table data for the specified organization based on the provided cost
   * definition request.
   *
   * <p>This method processes a POST request to fetch the rate card table data for a given
   * organization, using the provided {@link CostDefinitionRequest} to fetch the relevant data.
   *
   * @param orgId The unique identifier for the organization (e.g., "NEXTUPLE_GR").
   * @param costDefinitionRequest The request payload containing the cost definition details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the rate card table
   *     data.
   */
  @RateCardDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<CostDefinitionResponse>> getRateCardTableData(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @RequestBody CostDefinitionRequest costDefinitionRequest) {
    log.debug("Processing get rate card table for orgId {}", orgId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Cost config fetched successfully!")
                .payload(rateCardService.getRateCardTableData(orgId, costDefinitionRequest))
                .build());
  }
}
