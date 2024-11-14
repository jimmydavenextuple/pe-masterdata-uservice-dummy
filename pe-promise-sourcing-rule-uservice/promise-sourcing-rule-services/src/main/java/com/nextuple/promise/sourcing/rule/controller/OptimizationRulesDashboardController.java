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
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteOptimizationRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationRuleUpdationUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationStrategyUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.OptimizationRuleUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateOptimizationRuleDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteMultipleOptimizationRulesDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.EditOptimizationRuleDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAllOptimizationRulesByOrgIdDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAllUIConstraintsDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetOptimizationRuleByOrgIdAndNamedOptimazationStrategyIdDoc;
import com.nextuple.promise.sourcing.rule.service.NamedOptimizationStrategyService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
@RequestMapping("/ui/optimization-rules")
@RequiredArgsConstructor
@Tag(name = "Optimization Rule dashboard APIs")
public class OptimizationRulesDashboardController {

  private static final Logger logger =
      LoggerFactory.getLogger(OptimizationRulesDashboardController.class);

  private final NamedOptimizationStrategyService namedOptimizationStrategyService;

  @CreateOptimizationRuleDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/{orgId}")
  public ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> createOptimizationRule(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @Valid @RequestBody OptimizationStrategyUIRequest optimizationStrategyUIRequest)
      throws PromiseEngineException, CommonServiceException {
    try {
      var response =
          namedOptimizationStrategyService.createOptimizationRuleUI(
              orgId, optimizationStrategyUIRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization Rule created successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create Optimization Rule", e);
      throw e;
    }
  }

  @EditOptimizationRuleDoc
  @PutMapping(
      value = "/{orgId}/{optimizationRuleId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> editOptimizationRule(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "optimizationRuleId can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the optimization rule.")
          Long optimizationRuleId,
      @Valid @RequestBody OptimizationRuleUpdationUIRequest optimizationRuleUpdationUIRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing edit optimization rule request");
    try {
      var optimizationStrategyUpdatedResponse =
          namedOptimizationStrategyService.processEditOptimizationRuleUI(
              orgId, optimizationRuleId, optimizationRuleUpdationUIRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rule edited successfully")
              .payload(optimizationStrategyUpdatedResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process edit optimization rule request", e);
      throw e;
    }
  }

  @DeleteMultipleOptimizationRulesDoc
  @Transactional
  @DeleteMapping(value = "/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<OptimizationRuleUIResponse>>>
      deleteMultipleOptimizationStrategy(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @Valid @RequestBody DeleteOptimizationRulesRequest optimizationRuleIds)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete optimization strategy request");
    try {
      var namedOptimizationStrategyResponseList =
          namedOptimizationStrategyService.processDeleteMultipleOptimizationStrategy(
              orgId, optimizationRuleIds);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rules deleted successfully")
              .payload(namedOptimizationStrategyResponseList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete multiple optimization strategy request", e);
      throw e;
    }
  }

  @GetAllUIConstraintsDoc
  @GetMapping(value = "/constraints", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<AllConstraintUIDto>>> getAllUIConstraints() {
    logger.debug("Processing get all constraints request");
    try {
      var response = namedOptimizationStrategyService.getAllUIConstraints();
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Constraints fetched successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch constraints", e);
      throw e;
    }
  }

  @GetOptimizationRuleByOrgIdAndNamedOptimazationStrategyIdDoc
  @GetMapping(value = "/{orgId}/{optimizationRuleId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<OptimizationRuleUIResponse>>
      getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
          @NotEmpty(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(message = "optimizationRuleId can't be empty")
              @Min(value = 0)
              @PathVariable
              @Parameter(
                  description = "Unique identifier for the optimization rule.",
                  example = "1")
              Long optimizationRuleId)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get optimization rule by orgId and id request");
    try {

      var response =
          namedOptimizationStrategyService.getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
              orgId, optimizationRuleId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rule fetched successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch optimization rule", e);
      throw e;
    }
  }

  @GetAllOptimizationRulesByOrgIdDoc
  @GetMapping(value = "/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PageResponse<OptimizationRuleUIResponse>>>
      getAllOptimizationRulesByOrgId(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId,
          PageParams pageParams)
          throws CommonServiceException, PromiseEngineException {
    try {
      logger.debug("Processing get optimization rules by orgId with pagination");
      var optimizationRulesUIResponsePagePayload =
          namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(orgId, pageParams);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Optimization rules fetched successfully")
              .payload(optimizationRulesUIResponsePagePayload)
              .build());

    } catch (Exception e) {
      logger.error("Failed to fetch optimization rules", e);
      throw e;
    }
  }
}
