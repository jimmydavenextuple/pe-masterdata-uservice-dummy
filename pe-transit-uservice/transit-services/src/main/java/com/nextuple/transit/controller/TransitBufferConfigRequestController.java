/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.controller.docs.DeleteTransitBufferConfigRequestDoc;
import com.nextuple.transit.controller.docs.GetTransitBufferConfigRequestsDoc;
import com.nextuple.transit.controller.docs.ProcessTransitBufferConfigRequestDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferConfigRequestStatusDoc;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.service.TransitBufferConfigRequestService;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Transit Buffer Configuration Request APIs")
@RequestMapping("/transit/buffer-config-request")
@RequiredArgsConstructor
public class TransitBufferConfigRequestController {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferConfigRequestController.class);
  private final TransitBufferConfigRequestService transitBufferConfigRequestService;

  @ProcessTransitBufferConfigRequestDoc
  @PostMapping
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>>
      processTransitBufferConfigRequest(
          @Valid @RequestBody TransitBufferConfigRequest transitBufferConfigRequest)
          throws CommonServiceException,
              IOException,
              TransitBufferReqJobRefDomainException,
              CsvException {
    logger.debug("Processing transit buffer config creation request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config request successfully created")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process transit buffer config request");
      throw e;
    }
  }

  @UpdateTransitBufferConfigRequestStatusDoc
  @PutMapping("/update-status/{id}")
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>>
      updateTransitBufferConfigRequestStatus(
          @PathVariable
              @Parameter(
                  description = "Unique identifier of the transit buffer request.",
                  example = "1231231231")
              Long id,
          @RequestParam
              @NotNull
              @Valid
              @Parameter(
                  description = "Valid status of the transit buffer configuration request.",
                  example = "CREATED")
              TransitBufferConfigRequestStatusEnum status)
          throws CommonServiceException {
    logger.debug("Processing transit buffer config update status request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.updateTransitBufferRequestStatus(id, status);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config status updation request successfully processed")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process transit buffer config status updation request");
      throw e;
    }
  }

  @GetTransitBufferConfigRequestsDoc
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>>
      getTransitBufferConfigRequests(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @PathVariable
              String orgId,
          @Parameter(
                  description = "Unique identifier of the carrier service.",
                  example = "UPS-GROUND")
              @NotBlank(message = "carrierServiceId can't be blank")
              @RequestParam
              String carrierServiceId)
          throws CommonServiceException {
    logger.debug("Processing get transit buffer config requests");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.fetchTransitBufferRequests(orgId, carrierServiceId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config requests successfully fetched")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get transit buffer config requests");
      throw e;
    }
  }

  @DeleteTransitBufferConfigRequestDoc
  @DeleteMapping
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>> deleteTransitBufferConfigRequest(
      @Parameter(
              description = "Unique identifier of the transit buffer request.",
              example = "1231231231")
          @NotNull(message = "transitBufferRequestId can't be null")
          @RequestParam
          Long transitBufferRequestId,
      @Parameter(description = "User who created the request.", example = "user@email.com")
          @NotBlank(message = "createdBy can't be blank")
          @RequestParam
          String createdBy)
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
          CsvException {
    logger.debug("Processing transit buffer config creation request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.deleteTransitBufferRequest(
              transitBufferRequestId, createdBy);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config request successfully deleted")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete transit buffer config request");
      throw e;
    }
  }
}
