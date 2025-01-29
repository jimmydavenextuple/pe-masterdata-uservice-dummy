/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUpdateRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateHolidayCutoffDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteHolidayCutoffDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.FetchHolidayCutoffDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.FetchHolidayCutoffRulesDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateHolidayCutoffDoc;
import com.nextuple.promise.sourcing.rule.service.HolidayCutoffService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing holiday cutoff operations.
 *
 * <p>This controller provides APIs for adding, updating, retrieving, and deleting holiday cutoff
 * data associated with specific organizations. These operations manage holiday cutoff rules and
 * override data used in the sourcing process.
 *
 * <p>The controller is tagged with "Holiday Cutoff APIs" for easy categorization in the API
 * documentation.
 */
@RestController
@RequestMapping("/holiday-cutoff")
@RequiredArgsConstructor
@Tag(name = "Holiday Cutoff APIs")
@Slf4j
public class HolidayCutoffController {
  private final HolidayCutoffService holidayCutoffService;

  /**
   * Creates a new holiday cutoff.
   *
   * <p>This method processes a POST request to create holiday cutoff override data based on the
   * provided request body.
   *
   * @param holidayCutoffRequest The request body containing details for the new holiday cutoff.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created holiday
   *     cutoff information.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws ParseException If there is an error parsing date-related fields.
   */
  @CreateHolidayCutoffDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<HolidayCutoffInfo>> createHolidayCutoff(
      @Valid @RequestBody HolidayCutoffRequest holidayCutoffRequest)
      throws CommonServiceException, PromiseEngineException, ParseException {
    log.debug("Processing holiday cutoff creation request {}", holidayCutoffRequest);
    var holidayCutoffResponse = holidayCutoffService.createHolidayCutoff(holidayCutoffRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff override data created successfully!")
            .payload(holidayCutoffResponse)
            .build());
  }

  /**
   * Fetches holiday cutoff rules.
   *
   * <p>This method processes a POST request to retrieve the holiday cutoff rules based on the
   * provided request body.
   *
   * @param holidayCutoffRulesRequest The request body containing the criteria for fetching holiday
   *     cutoff rules.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched holiday
   *     cutoff rules.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @FetchHolidayCutoffRulesDoc
  @PostMapping(
      value = "/fetch-rules",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<HolidayCutoffRulesResponse>> fetchHolidayCutoffRules(
      @Valid @RequestBody HolidayCutoffRulesRequest holidayCutoffRulesRequest)
      throws PromiseEngineException, CommonServiceException {
    log.debug("Processing fetch holiday cutoff rules request : {}", holidayCutoffRulesRequest);
    var holidayCutoffResponse =
        holidayCutoffService.processFetchHolidayCutoffRules(holidayCutoffRulesRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff rules fetched successfully!")
            .payload(holidayCutoffResponse)
            .build());
  }

  /**
   * Updates an existing holiday cutoff.
   *
   * <p>This method processes a PUT request to update the details of a holiday cutoff identified by
   * the organization ID, holiday cutoff name, and rule.
   *
   * @param orgId The unique identifier of the organization.
   * @param holidayCutoffName The name of the holiday cutoff.
   * @param holidayCutoffRule The colon-separated value of attributes defining the holiday cutoff
   *     rule.
   * @param holidayCutoffUpdateRequest The request body containing the updated details for the
   *     holiday cutoff.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated holiday
   *     cutoff information.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @UpdateHolidayCutoffDoc
  @PutMapping(
      value = "/{orgId}/{holidayCutoffName}/{holidayCutoffRule}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<HolidayCutoffInfo>> updateHolidayCutoff(
      @NotBlank(message = "Unique identifier for organisation can't be empty")
          @PathVariable("orgId")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "holidayCutoffName can't be empty")
          @PathVariable("holidayCutoffName")
          @Parameter(description = "Name of the holiday cutoff.", example = "Christmas")
          String holidayCutoffName,
      @NotBlank(message = "holidayCutoffRule can't be empty")
          @PathVariable("holidayCutoffRule")
          @Parameter(description = "Colon separated values of attributes.", example = "EXPRESS:T2P")
          String holidayCutoffRule,
      @NotBlank(message = "holidayCutoffUpdateRequest can't be empty") @Valid @RequestBody
          HolidayCutoffUpdateRequest holidayCutoffUpdateRequest)
      throws CommonServiceException {
    log.debug(
        "Processing holiday cutoff updation orgid {}, holidayCutoffName {}, holidayCutoffRule {} request {}",
        orgId,
        holidayCutoffName,
        holidayCutoffRule,
        holidayCutoffUpdateRequest);
    var holidayCutoffResponse =
        holidayCutoffService.updateHolidayCutoff(
            orgId, holidayCutoffName, holidayCutoffRule, holidayCutoffUpdateRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff override data updated successfully!")
            .payload(holidayCutoffResponse)
            .build());
  }

  /**
   * Fetches a specific holiday cutoff by organization ID, name, and rule.
   *
   * <p>This method processes a GET request to retrieve the details of a holiday cutoff identified
   * by the organization ID, holiday cutoff name, and rule.
   *
   * @param orgId The unique identifier of the organization.
   * @param holidayCutoffName The name of the holiday cutoff.
   * @param holidayCutoffRule The colon-separated value of attributes defining the holiday cutoff
   *     rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched holiday
   *     cutoff information.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @FetchHolidayCutoffDoc
  @GetMapping(
      value = "/{orgId}/{holidayCutoffName}/{holidayCutoffRule}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<HolidayCutoffInfo>> fetchHolidayCutoff(
      @NotBlank(message = "Unique identifier for organisation can't be empty") @PathVariable
          String orgId,
      @NotBlank(message = "holidayCutoffName can't be empty") @PathVariable
          String holidayCutoffName,
      @NotBlank(message = "holidayCutoffRule can't be empty") @PathVariable
          String holidayCutoffRule)
      throws CommonServiceException {
    log.debug(
        "Processing holiday cutoff fetch request orgid {}, holidayCutoffName {}, holidayCutoffRule {}",
        orgId,
        holidayCutoffName,
        holidayCutoffRule);
    var holidayCutoffResponse =
        holidayCutoffService.fetchHolidayCutoff(orgId, holidayCutoffName, holidayCutoffRule);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff data fetched successfully!")
            .payload(holidayCutoffResponse)
            .build());
  }

  /**
   * Deletes a specific holiday cutoff by organization ID, name, and rule.
   *
   * <p>This method processes a DELETE request to remove a holiday cutoff identified by the
   * organization ID, holiday cutoff name, and rule.
   *
   * @param orgId The unique identifier of the organization.
   * @param holidayCutoffName The name of the holiday cutoff.
   * @param holidayCutoffRule The colon-separated value of attributes defining the holiday cutoff
   *     rule.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the deleted holiday
   *     cutoff information.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
  @DeleteHolidayCutoffDoc
  @DeleteMapping(
      value = "/{orgId}/{holidayCutoffName}/{holidayCutoffRule}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<HolidayCutoffInfo>> deleteHolidayCutoff(
      @NotBlank(message = "Unique identifier for organisation can't be empty") @PathVariable
          String orgId,
      @NotBlank(message = "holidayCutoffName can't be empty") @PathVariable
          String holidayCutoffName,
      @NotBlank(message = "holidayCutoffRule can't be empty") @PathVariable
          String holidayCutoffRule)
      throws CommonServiceException {
    log.debug(
        "Processing holiday cutoff deletion orgid {}, holidayCutoffName {}, holidayCutoffRule {} request {}",
        orgId,
        holidayCutoffName,
        holidayCutoffRule);
    var holidayCutoffResponse =
        holidayCutoffService.deleteHolidayCutoff(orgId, holidayCutoffName, holidayCutoffRule);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Holiday cutoff data deleted successfully!")
            .payload(holidayCutoffResponse)
            .build());
  }
}
