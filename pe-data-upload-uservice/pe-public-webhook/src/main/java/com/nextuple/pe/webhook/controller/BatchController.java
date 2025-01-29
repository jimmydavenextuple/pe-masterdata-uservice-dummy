/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.webhook.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.pe.webhook.service.BatchProcessingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling batch processing operations.
 *
 * <p>This controller provides APIs to process batch records for various modules. It requires tenant
 * identification and ensures proper validation of incoming batch requests.
 *
 * <p>Tagged under "Batch API" for documentation categorization, this controller streamlines batch
 * processing with robust exception handling and logging.
 */
@RequestMapping("/batch-request")
@RestController
@Tag(name = "Batch API")
@RequiredArgsConstructor
@Validated
public class BatchController {
  private static final Logger logger = LoggerFactory.getLogger(BatchController.class);
  private static final String TENANT = "x-tenant-id";
  private static final String TENANT_MISSING_MESSAGE = "Tenant ID not passed";
  private final BatchProcessingService batchProcessingService;

  /**
   * Processes a batch of records for a specified module.
   *
   * <p>This method accepts a list of batch records and processes them using the specified module's
   * business logic. It requires a tenant ID to identify the organization and a module name to
   * determine which module the request pertains to.
   *
   * @param orgId The unique identifier for the organization (tenant).
   * @param moduleName The name of the module where the batch records need to be processed.
   * @param batchFeed The list of batch records to be processed, validated using `@Valid`.
   * @return A response containing the result of the batch processing.
   * @throws CommonServiceException If any error occurs during batch processing.
   */
  @PostMapping("/{moduleName}")
  public ResponseEntity<BaseResponse<BatchResponse>> batchApi(
      @NotBlank(message = TENANT_MISSING_MESSAGE) @RequestHeader(value = TENANT) String orgId,
      @PathVariable String moduleName,
      @NotEmpty(message = "batch records cannot be empty") @Valid @RequestBody
          List<@Valid BatchRequest<?>> batchFeed)
      throws CommonServiceException {
    try {
      BatchResponse batchResponse =
          batchProcessingService.processRecords(moduleName, batchFeed, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Batch call processed successfully")
              .payload(batchResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process batch call");
      throw e;
    }
  }
}
