/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchRuleConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.RuleConfigurationParam;
import com.nextuple.promise.sourcing.rule.service.RulesConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RulesConfigurationControllerTest {

  @Mock private RulesConfigurationService rulesConfigurationService;

  @InjectMocks private RulesConfigurationController rulesConfigurationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Create Rules Configuration - Success test")
  void createRulesConfigurationSuccessTest() throws PromiseEngineException, CommonServiceException {

    when(rulesConfigurationService.createRulesConfiguration(any(RulesConfigurationsRequest.class)))
        .thenReturn(testUtil.getExpectedRulesConfigurationResponse1());

    ResponseEntity<BaseResponse<RulesConfigurationResponse>> responseEntity =
        rulesConfigurationController.createRulesConfiguration(
            testUtil.getRulesConfigurationForMLRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Rule configuration created successfully!", responseEntity.getBody().getMessage());
    assertEquals("NEXTUPLE", responseEntity.getBody().getPayload().getOrgId());
    assertEquals("Rule-1", responseEntity.getBody().getPayload().getRuleName());

    verify(rulesConfigurationService, times(1))
        .createRulesConfiguration(any(RulesConfigurationsRequest.class));
  }

  @Test
  @DisplayName("Create Rules Configuration - Exception test")
  void createRulesConfigurationExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(rulesConfigurationService.createRulesConfiguration(any(RulesConfigurationsRequest.class)))
        .thenThrow(new RuntimeException("An error occurred while creating the rule configuration"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                rulesConfigurationController.createRulesConfiguration(
                    testUtil.getRulesConfigurationForMLRequest()));

    assertEquals("An error occurred while creating the rule configuration", ex.getMessage());

    verify(rulesConfigurationService, times(1))
        .createRulesConfiguration(any(RulesConfigurationsRequest.class));
  }

  @Test
  @DisplayName("Fetch rules configuration: Happy Path")
  void fetchRulesConfigurationSuccessTest() throws PromiseEngineException, CommonServiceException {
    when(rulesConfigurationService.processFetchRulesConfiguration(
            any(FetchRuleConfigurationRequest.class)))
        .thenReturn(testUtil.getExpectedRulesConfigurationResponse1());

    ResponseEntity<BaseResponse<RulesConfigurationResponse>> responseEntity =
        rulesConfigurationController.fetchRulesConfiguration(
            testUtil.getFetchRuleConfigurationRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Rule configuration fetched successfully!", responseEntity.getBody().getMessage());
    assertEquals("NEXTUPLE", responseEntity.getBody().getPayload().getOrgId());
    assertEquals("Rule-1", responseEntity.getBody().getPayload().getRuleName());

    verify(rulesConfigurationService, times(1))
        .processFetchRulesConfiguration(any(FetchRuleConfigurationRequest.class));
  }

  @Test
  @DisplayName("Fetch rules configuration: Exception Path")
  void fetchRulesConfigurationExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(rulesConfigurationService.processFetchRulesConfiguration(
            any(FetchRuleConfigurationRequest.class)))
        .thenThrow(new RuntimeException("An error occurred while fetching the rule configuration"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                rulesConfigurationController.fetchRulesConfiguration(
                    testUtil.getFetchRuleConfigurationRequest()));

    assertEquals("An error occurred while fetching the rule configuration", ex.getMessage());

    verify(rulesConfigurationService, times(1))
        .processFetchRulesConfiguration(any(FetchRuleConfigurationRequest.class));
  }

  @Test
  @DisplayName("Delete rules configuration: Happy Path")
  void deleteRuleConfigurationSuccessTest() throws PromiseEngineException, CommonServiceException {
    when(rulesConfigurationService.deleteRuleConfiguration(any(RuleConfigurationParam.class)))
        .thenReturn(testUtil.getExpectedRulesConfigurationResponse1());

    ResponseEntity<BaseResponse<RulesConfigurationResponse>> responseEntity =
        rulesConfigurationController.deleteRuleConfiguration(testUtil.getRuleConfigurationParam());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals("Rule configuration deleted successfully!", responseEntity.getBody().getMessage());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getOrgId(),
        responseEntity.getBody().getPayload().getOrgId());
    assertEquals(
        testUtil.getExpectedRulesConfigurationResponse1().getRuleName(),
        responseEntity.getBody().getPayload().getRuleName());

    verify(rulesConfigurationService, times(1))
        .deleteRuleConfiguration(any(RuleConfigurationParam.class));
  }

  @Test
  @DisplayName("Delete rules configuration: Exception Path")
  void deleteRuleConfigurationExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(rulesConfigurationService.deleteRuleConfiguration(any(RuleConfigurationParam.class)))
        .thenThrow(new RuntimeException("An error occurred while deleting the rule configuration"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                rulesConfigurationController.deleteRuleConfiguration(
                    testUtil.getRuleConfigurationParam()));

    assertEquals("An error occurred while deleting the rule configuration", ex.getMessage());

    verify(rulesConfigurationService, times(1))
        .deleteRuleConfiguration(any(RuleConfigurationParam.class));
  }
}
