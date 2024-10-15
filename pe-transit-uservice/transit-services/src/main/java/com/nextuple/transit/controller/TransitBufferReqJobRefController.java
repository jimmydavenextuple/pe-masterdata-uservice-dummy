/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.controller.docs.FindTransitBufferReqJobRefByExtRefIdDoc;
import com.nextuple.transit.controller.docs.TransitBufferReqJobRefRequestDoc;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.service.TransitBufferReqJobRefService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transit/transit-buffer-req-jobs-reference")
@RequiredArgsConstructor
@Tag(name = "Transit Buffer Request APIs")
public class TransitBufferReqJobRefController {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferReqJobRefController.class);

  private final TransitBufferReqJobRefService transitBufferReqJobRefService;

  @TransitBufferReqJobRefRequestDoc
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferReqJobRefResponse>> createTransitBufferReqJobRef(
      @Valid @RequestBody TransitBufferReqJobRefRequest transitBufferReqJobRefRequest)
      throws TransitBufferReqJobRefDomainException {
    logger.debug("Processing transit buffer request job reference creation request");
    try {
      var response =
          transitBufferReqJobRefService.createTransitBufferReqJobRef(transitBufferReqJobRefRequest);
      logger.info("Response after creation of transit buffer request job reference:");
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("transit buffer request job reference created successfully")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(
          e.getMessage(),
          transitBufferReqJobRefRequest.getTransitBufferReqId(),
          transitBufferReqJobRefRequest.getExtReferenceId());
    }
  }

  @FindTransitBufferReqJobRefByExtRefIdDoc
  @GetMapping(path = "/{extReferenceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitBufferReqJobRefResponse>>>
      findTransitBufferReqJobRefByExtRefId(
          @Parameter(
                  description = "Unique identifier for external reference or a job.",
                  example = "ref-123")
              @NotBlank
              @PathVariable
              String extReferenceId)
          throws TransitBufferReqJobRefDomainException {
    try {
      var response =
          transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(extReferenceId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer request job reference fetched successfully")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(e.getMessage(), null, extReferenceId);
    }
  }
}
