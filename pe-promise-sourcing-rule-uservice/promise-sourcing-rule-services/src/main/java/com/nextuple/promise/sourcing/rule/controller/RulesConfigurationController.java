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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchRuleConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteRuleConfigurationDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.FetchRulesConfigurationDoc;
import com.nextuple.promise.sourcing.rule.service.RulesConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing rules configurations within the organization.
 *
 * <p>This controller provides APIs for creating, fetching, and deleting rule configurations related
 * to promise sourcing. Rule configurations define the specific parameters and criteria used in
 * sourcing rules within an organization.
 *
 * <p>The controller is tagged with "Rules Configuration APIs" for easy categorization in the API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Rules Configuration APIs")
@RequestMapping("/rules-configuration")
@RequiredArgsConstructor
@Slf4j
public class RulesConfigurationController {
  private final RulesConfigurationService rulesConfigurationService;

  /**
   * Creates a new rule configuration.
   *
   * <p>Processes a POST request to create a rule configuration based on the provided details.
   *
   * @param request The {@link RulesConfigurationsRequest} containing details for the new rule
   *     configuration.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created rule
   *     configuration details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @PostMapping
  public ResponseEntity<BaseResponse<RulesConfigurationResponse>> createRulesConfiguration(
      @Valid @RequestBody RulesConfigurationsRequest request)
      throws PromiseEngineException, CommonServiceException {
    log.debug("Processing create rule configuration with rulesConfigurationsRequest : {}", request);
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Rule configuration created successfully!")
              .payload(rulesConfigurationService.createRulesConfiguration(request))
              .build());
    } catch (Exception e) {
      log.error("Failed to process create rule configuration");
      throw e;
    }
  }

  /**
   * Fetches an existing rule configuration.
   *
   * <p>Processes a POST request to fetch a rule configuration based on the provided criteria.
   *
   * @param request The {@link FetchRuleConfigurationRequest} containing criteria for fetching the
   *     rule configuration.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched rule
   *     configuration details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @FetchRulesConfigurationDoc
  @PostMapping("/fetch-rule")
  public ResponseEntity<BaseResponse<RulesConfigurationResponse>> fetchRulesConfiguration(
      @Valid @RequestBody FetchRuleConfigurationRequest request)
      throws PromiseEngineException, CommonServiceException {
    log.debug("Processing fetch rule configuration with rulesConfigurationsRequest : {}", request);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Rule configuration fetched successfully!")
            .payload(rulesConfigurationService.processFetchRulesConfiguration(request))
            .build());
  }

  /**
   * Deletes an existing rule configuration.
   *
   * <p>Processes a DELETE request to delete a rule configuration based on the provided parameters.
   *
   * @param ruleConfigurationParam The {@link RuleConfigurationParam} containing details of the rule
   *     configuration to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} confirming the deletion.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @DeleteRuleConfigurationDoc
  @DeleteMapping
  public ResponseEntity<BaseResponse<RulesConfigurationResponse>> deleteRuleConfiguration(
      @Valid RuleConfigurationParam ruleConfigurationParam)
      throws PromiseEngineException, CommonServiceException {
    log.debug("Processing delete rule configuration with params : {}", ruleConfigurationParam);
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Rule configuration deleted successfully!")
              .payload(rulesConfigurationService.deleteRuleConfiguration(ruleConfigurationParam))
              .build());
    } catch (Exception e) {
      log.error("Failed to process delete rule configuration");
      throw e;
    }
  }
}
