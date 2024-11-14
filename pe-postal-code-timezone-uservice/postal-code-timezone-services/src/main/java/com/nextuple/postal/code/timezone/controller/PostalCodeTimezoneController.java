/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.controller.docs.CreatePostalCodeTimezoneDetails;
import com.nextuple.postal.code.timezone.controller.docs.DeletePostalCodeTimezoneDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodePrefixListDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodeTimezoneDetails;
import com.nextuple.postal.code.timezone.controller.docs.UpdatePostalCodeTimezoneDetails;
import com.nextuple.postal.code.timezone.service.PostalCodeTimezoneService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@Tag(name = "Postal Code Timezone APIs")
@RequestMapping("/postalCodeTimezone")
@RequiredArgsConstructor
public class PostalCodeTimezoneController {

  private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneController.class);
  private final PostalCodeTimezoneService postalCodeTimezoneService;

  @CreatePostalCodeTimezoneDetails
  @PostMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> createPostalCodeTimezone(
      @Valid @RequestBody CreatePostalCodeTimezoneRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Zip Code Timezone request");
    try {
      var postalCodeTimezoneDto = postalCodeTimezoneService.createPostalCodeTimezone(baseRequest);
      logger.info("Response after creation of zip-code timezone :{}", postalCodeTimezoneDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code Timezone successfully created!")
              .payload(postalCodeTimezoneDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Zip Code Timezone request!");
      throw e;
    }
  }

  @GetPostalCodeTimezoneDetails
  @GetMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> getPostalCodeTimezone(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "zipCodePrefix can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "First three characters of the zip code of the source or destination node.",
              example = "T2P")
          String zipCodePrefix)
      throws PromiseEngineException {
    logger.debug("Processing get Zip Code Timezone request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("zip Code Timezone successfully fetched!")
              .payload(postalCodeTimezoneService.getPostalCodeTimezone(orgId, zipCodePrefix))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Zip Code Timezone request!");
      throw e;
    }
  }

  @UpdatePostalCodeTimezoneDetails
  @PutMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> updatePostalCodeTimezone(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "zipCodePrefix can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "First three characters of the zip code of the source or destination node.",
              example = "T2P")
          String zipCodePrefix,
      @Valid @RequestBody UpdatePostalCodeTimezoneRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update Zip Code Timezone request");
    try {
      var postalCodeTimezoneDto =
          postalCodeTimezoneService.updatePostalCodeTimezone(orgId, zipCodePrefix, baseRequest);
      logger.info("Response after updation of zip-code timezone :{}", postalCodeTimezoneDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code Timezone successfully updated!")
              .payload(postalCodeTimezoneDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Zip Code Timezone request!");
      throw e;
    }
  }

  @DeletePostalCodeTimezoneDetails
  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> deletePostalCodeTimezone(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "zipCodePrefix can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "First three characters of the zip code of the source or destination node.",
              example = "T2P")
          String zipCodePrefix)
      throws PromiseEngineException {
    logger.debug("Processing delete Zip Code Timezone request");
    try {
      var postalCodeTimezoneDto =
          postalCodeTimezoneService.deletePostalCodeTimezone(orgId, zipCodePrefix);
      logger.info("Response after deletion of zip-code timezone :{}", postalCodeTimezoneDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code Timezone successfully deleted!")
              .payload(postalCodeTimezoneDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Zip Code Timezone request!");
      throw e;
    }
  }

  @GetPostalCodePrefixListDetails
  @GetMapping("/ui/state-postal-code-prefix/orgId/{orgId}")
  public ResponseEntity<BaseResponse<List<PostalCodePrefixDto>>> getPostalCodePrefixList(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException {
    logger.debug("Processing get state and zip code prefixes list");

    List<PostalCodePrefixDto> responseList =
        postalCodeTimezoneService.fetchPostalCodePrefixList(orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("State and Zip Code Prefix list fetched successfully")
            .payload(responseList)
            .build());
  }
}
