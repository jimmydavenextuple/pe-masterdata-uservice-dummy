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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchSourcingRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.controller.docs.ConfigureSourcingRuleDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteSourcingRuleDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.FetchSourcingRulesDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetSourcingRuleDoc;
import com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/sourcing-rules-configuration")
@RequiredArgsConstructor
@Tag(name = "Sourcing Rules Configuration APIs")
public class SourcingRulesConfigurationController {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingRulesConfigurationController.class);

  private final SourcingRulesConfigurationService sourcingRulesConfigurationService;

  @ConfigureSourcingRuleDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingRuleDetails>> configureSourcingRule(
      @Valid @RequestBody RulesConfigurationRequest rulesConfigurationRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing configure sourcing rule");
    try {
      var sourcingRuleDetails =
          sourcingRulesConfigurationService.processConfigureSourcingRule(rulesConfigurationRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rule successfully configured")
              .payload(sourcingRuleDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process configure sourcing rule", e);
      throw e;
    }
  }

  @GetSourcingRuleDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingRuleDetails>> getSourcingRuleDetailsByIdandOrgId(
      @NotNull(message = "id can't be empty")
          @Min(value = 0)
          @PathVariable
          @Parameter(
              description = "Unique identifier for sourcing rules configuration",
              example = "1")
          Long id,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get sourcing rule details by id request");
    try {
      var sourcingRuleDetails =
          sourcingRulesConfigurationService.processGetSourcingRuleDetailsByIdandOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rule details fetched successfully")
              .payload(sourcingRuleDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get sourcing rule details by id request", e);
      throw e;
    }
  }

  @DeleteSourcingRuleDoc
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingRuleDetails>> deleteSourcingRule(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "sourcingRuleId can't be null") @RequestParam Long sourcingRuleId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete sourcing rule");
    try {
      SourcingRuleDetails sourcingRuleDetails =
          sourcingRulesConfigurationService.processDeleteSourcingRuleDetails(orgId, sourcingRuleId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rule successfully deleted")
              .payload(sourcingRuleDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete sourcing rule", e);
      throw e;
    }
  }

  @FetchSourcingRulesDoc
  @PostMapping(
      value = "/fetch-rules",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<FetchSourcingRulesResponse>> fetchSourcingRules(
      @Valid @RequestBody FetchSourcingRulesRequest fetchSourcingRulesRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing fetch sourcing rules for given order");
    try {
      var sourcingRuleDetails =
          sourcingRulesConfigurationService.processGetSourcingRules(fetchSourcingRulesRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing rules successfully fetched for given order")
              .payload(sourcingRuleDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch sourcing rules for given order", e);
      throw e;
    }
  }
}
