/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.transit.controller.docs.CreateTransitBufferDoc;
import com.nextuple.transit.controller.docs.DeleteTransitBufferDetailsDoc;
import com.nextuple.transit.controller.docs.GetByOrgIdAndDestinationGeozoneDoc;
import com.nextuple.transit.controller.docs.GetTransitBufferDetailsDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferDoc;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.service.TransitBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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

@RestController
@RequestMapping("/transit/v1/buffer")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transit Buffer APIs")
public class TransitBufferController {

  private final TransitBufferService transitBufferService;

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferController.class);

  @CreateTransitBufferDoc
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferResponse>> createTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferService.saveTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer created successfully")
            .payload(response)
            .build());
  }

  @GetByOrgIdAndDestinationGeozoneDoc
  @GetMapping(path = "/org/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitBufferResponse>>> getByOrgIdAndDestinationGeozone(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Destination geo zone of the transit.", example = "H1R")
          @NotBlank(message = "destinationGeozone can't be blank")
          @RequestParam
          String destinationGeozone)
      throws CommonServiceException {
    logger.debug("Processing get all transit buffers by orgId and destination geozone");
    var response =
        transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
            orgId, destinationGeozone);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details fetched successfully")
            .payload(response)
            .build());
  }

  @UpdateTransitBufferDoc
  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferResponse>> updateTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferService.updateTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer updated successfully")
            .payload(response)
            .build());
  }

  @DeleteTransitBufferDetailsDoc
  @DeleteMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferResponse>> deleteTransitBufferDetails(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.debug("Processing delete transit buffer details");

    var response =
        transitBufferService.deleteTransitBufferDetails(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getCarrierServiceId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone());

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer deleted successfully")
            .payload(response)
            .build());
  }

  @GetTransitBufferDetailsDoc
  @GetMapping("/{transitBufferConfigRequestId}")
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> getTransitBufferDetails(
      @Parameter(
              description = "Unique identifier of the transit buffer configuration request.",
              example = "1231231231")
          @PathVariable
          Long transitBufferConfigRequestId,
      @Parameter(description = "User who created the request.", example = "user@email.com")
          @NotBlank(message = "createdBy can't be empty")
          @RequestParam
          String createdBy)
      throws IOException, CommonServiceException {
    logger.debug("Processing get transit buffer details by transitBufferRequestId");

    var response =
        transitBufferService.getTransitBufferDetails(transitBufferConfigRequestId, createdBy);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details with pre signed url fetched successfully")
            .payload(response)
            .build());
  }
}
