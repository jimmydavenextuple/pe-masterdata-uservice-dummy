/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.pe.webhook.service.MasterDataIngestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing master data ingestion operations.
 *
 * <p>This controller provides APIs to process and publish master data feeds for various modules. It
 * ensures proper validation of incoming requests and handles exceptions with appropriate logging
 * and response mechanisms.
 *
 * <p>Tagged under "INge API" for documentation categorization, this controller facilitates seamless
 * data ingestion with a focus on modularity and organizational context.
 */
@RequestMapping("/ingest-data")
@RestController
@Tag(name = "INge API")
@RequiredArgsConstructor
@Validated
public class MasterDataIngestionController {
  private static final Logger logger = LoggerFactory.getLogger(MasterDataIngestionController.class);
  private static final String TENANT = "x-tenant-id";
  private static final String TENANT_MISSING_MESSAGE = "Tenant ID not passed";
  private final MasterDataIngestionService masterDataIngestionService;

  /**
   * Publishes the master data feed for the specified module.
   *
   * <p>This method processes and publishes the master data ingestion request for a given module. It
   * validates the input data, interacts with the service layer, and provides a response indicating
   * the success of the operation.
   *
   * @param orgId The unique identifier for the organization, passed in the request header.
   * @param moduleName The name of the module for which the master data is being ingested.
   * @param masterDataIngestionRequest The feed request containing the master data to be ingested.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message
   *     indicating the successful publication of the master data feed.
   * @throws CommonServiceException If there is any error during the ingestion process.
   */
  @PostMapping("/{moduleName}")
  public ResponseEntity<BaseResponse<String>> publishMasterData(
      @NotBlank(message = TENANT_MISSING_MESSAGE) @RequestHeader(value = TENANT) String orgId,
      @PathVariable String moduleName,
      @Valid @RequestBody FeedRequest<MasterDataIngestionDto<?>> masterDataIngestionRequest)
      throws CommonServiceException {
    masterDataIngestionService.processMasterDataIngestionData(
        moduleName, masterDataIngestionRequest, orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .success(true)
            .message("Master data feed published successfully")
            .build());
  }
}
