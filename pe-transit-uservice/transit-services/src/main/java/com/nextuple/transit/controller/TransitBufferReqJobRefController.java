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

/**
 * Controller for managing Transit Buffer Request Job References. This includes creating and
 * retrieving transit buffer request job references.
 *
 * <p>The Transit Buffer Request Job Reference Controller provides API endpoints for creating a new
 * transit buffer request job reference and retrieving transit buffer request job references by
 * external reference ID.
 *
 * <p>The controller is tagged with "Transit Buffer Request APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/transit/transit-buffer-req-jobs-reference")
@RequiredArgsConstructor
@Tag(name = "Transit Buffer Request APIs")
public class TransitBufferReqJobRefController {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferReqJobRefController.class);

  private final TransitBufferReqJobRefService transitBufferReqJobRefService;

  /**
   * Creates a new Transit Buffer Request Job Reference.
   *
   * <p>This method processes a POST request to create a new transit buffer request job reference.
   * It consumes a JSON payload in the request body, performs the necessary business logic using the
   * {@link TransitBufferReqJobRefService}, and returns a response containing the result of the
   * creation process.
   *
   * @param transitBufferReqJobRefRequest The request payload containing the details needed for
   *     creating the transit buffer request job reference.
   * @return A {@link ResponseEntity} containing the {@link BaseResponse} with the payload of the
   *     created transit buffer request job reference and a success message.
   * @throws TransitBufferReqJobRefDomainException If an error occurs while creating the transit
   *     buffer request job reference, a custom exception is thrown with details about the error and
   *     relevant identifiers.
   */
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

  /**
   * Retrieves a Transit Buffer Request Job Reference for a given external reference ID.
   *
   * <p>This method processes a GET request to retrieve the transit buffer request job reference
   * associated with the provided external reference ID. It returns a list of matching job
   * references in the response payload.
   *
   * @param extReferenceId The unique identifier for the external reference or job. Example:
   *     "ref-123"
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     TransitBufferReqJobRefResponse} objects and a success message.
   * @throws TransitBufferReqJobRefDomainException If an error occurs while retrieving the transit
   *     buffer request job reference, a custom exception is thrown with details about the error and
   *     the relevant external reference ID.
   */
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
