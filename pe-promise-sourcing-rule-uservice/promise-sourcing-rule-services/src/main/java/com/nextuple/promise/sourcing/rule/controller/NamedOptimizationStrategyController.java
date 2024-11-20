/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NamedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.AddOptimizationStrategyDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteOptimizationStrategyDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetOptimizationStrategyByIdDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetOptimizationStrategyByOrgIdAndGroupIdDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateOptimizationStrategyDoc;
import com.nextuple.promise.sourcing.rule.service.NamedOptimizationStrategyService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/named-optimization-strategy")
@RequiredArgsConstructor
@Tag(name = "Named Optimization Strategy APIs")
public class NamedOptimizationStrategyController {

  private static final Logger logger =
      LoggerFactory.getLogger(NamedOptimizationStrategyController.class);

  private final NamedOptimizationStrategyService namedOptimizationStrategyService;

  @AddOptimizationStrategyDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> addOptimizationStrategy(
      @Valid @RequestBody NamedOptimizationStrategyRequest namedOptimizationStrategyRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing add optimization strategy request");
    try {
      var namedOptimizationStrategyResponse =
          namedOptimizationStrategyService.processAddOptimizationStrategy(
              namedOptimizationStrategyRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Named optimization strategy successfully added")
              .payload(namedOptimizationStrategyResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process add optimization strategy request", e);
      throw e;
    }
  }

  @GetOptimizationStrategyByIdDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>>
      getOptimizationStrategyByOrgIdAndId(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(message = "id can't be null")
              @Min(value = 0)
              @PathVariable
              @Parameter(
                  description = "Unique identifier for the named optimization strategy.",
                  example = "1")
              Long id)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get optimization strategy by id request");
    try {
      var namedOptimizationStrategyResponse =
          namedOptimizationStrategyService.processGetOptimizationStrategyByIdAndOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Named optimization strategy for given id successfully fetched")
              .payload(namedOptimizationStrategyResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get optimization strategy by id request", e);
      throw e;
    }
  }

  @GetOptimizationStrategyByOrgIdAndGroupIdDoc
  @GetMapping(
      value = "/orgId/{orgId}/groupId/{groupId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<DetailedOptimizationStrategyResponse>>
      getOptimizationStrategyByOrgIdAndGroupId(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(message = "groupId can't be blank")
              @PathVariable
              @Parameter(description = "Reference to the group.")
              String groupId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get optimization strategy by orgId and groupId request");
    try {
      var namedOptimizationStrategyResponse =
          namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
              orgId, groupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message(
                  "Named optimization strategy for given orgId and groupId successfully fetched")
              .payload(namedOptimizationStrategyResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get optimization strategy by orgId and groupId request", e);
      throw e;
    }
  }

  @UpdateOptimizationStrategyDoc
  @PutMapping(
      value = "/orgId/{orgId}/groupId/{groupId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> updateOptimizationStrategy(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "groupId can't be blank")
          @PathVariable
          @Parameter(description = "Reference to the group.")
          String groupId,
      @Valid @RequestBody
          NamedOptimizationStrategyUpdationRequest namedOptimizationStrategyUpdationRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update optimization strategy request");
    try {
      var namedOptimizationStrategyResponse =
          namedOptimizationStrategyService.processUpdateOptimizationStrategy(
              orgId, groupId, namedOptimizationStrategyUpdationRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Named optimization strategy successfully updated")
              .payload(namedOptimizationStrategyResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update optimization strategy request", e);
      throw e;
    }
  }

  @DeleteOptimizationStrategyDoc
  @DeleteMapping(
      value = "/orgId/{orgId}/groupId/{groupId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> deleteOptimizationStrategy(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "groupId can't be blank")
          @PathVariable
          @Parameter(description = "Reference to the group.")
          String groupId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete optimization strategy request");
    try {
      var namedOptimizationStrategyResponse =
          namedOptimizationStrategyService.processDeleteOptimizationStrategy(orgId, groupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Named optimization strategy successfully deleted")
              .payload(namedOptimizationStrategyResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete optimization strategy request", e);
      throw e;
    }
  }
}
