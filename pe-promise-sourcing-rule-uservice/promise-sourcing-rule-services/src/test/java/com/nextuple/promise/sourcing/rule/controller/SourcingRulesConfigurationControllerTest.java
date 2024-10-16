/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchSourcingRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.RulesConfigurationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingAttributeValuesInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SourcingRulesConfigurationControllerTest {

  @Mock private SourcingRulesConfigurationService sourcingRulesConfigurationService;

  @InjectMocks private SourcingRulesConfigurationController sourcingRulesConfigurationController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void configureSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    SourcingRuleDetails sourcingRuleDetails = testUtil.getSourcingRuleDetails();
    when(sourcingRulesConfigurationService.processConfigureSourcingRule(
            any(RulesConfigurationRequest.class)))
        .thenReturn(sourcingRuleDetails);

    ResponseEntity<BaseResponse<SourcingRuleDetails>> responseEntity =
        sourcingRulesConfigurationController.configureSourcingRule(
            testUtil.getRulesConfigurationRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingRuleDetails.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingRulesConfigurationService, times(1))
        .processConfigureSourcingRule(any(RulesConfigurationRequest.class));
  }

  @Test
  void configureSourcingRuleExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(sourcingRulesConfigurationService.processConfigureSourcingRule(
            any(RulesConfigurationRequest.class)))
        .thenThrow(new RuntimeException("Error in configuring sourcing rule"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingRulesConfigurationController.configureSourcingRule(
                    testUtil.getRulesConfigurationRequest()));
    assertEquals("Error in configuring sourcing rule", ex.getMessage());

    verify(sourcingRulesConfigurationService, times(1))
        .processConfigureSourcingRule(any(RulesConfigurationRequest.class));
  }

  @Test
  void getSourcingRuleByIdandOrgIdTest() throws PromiseEngineException, CommonServiceException {

    SourcingRuleDetails sourcingRuleDetails = testUtil.getSourcingRuleDetails();
    when(sourcingRulesConfigurationService.processGetSourcingRuleDetailsByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingRuleDetails);

    ResponseEntity<BaseResponse<SourcingRuleDetails>> responseEntity =
        sourcingRulesConfigurationController.getSourcingRuleDetailsByIdandOrgId(
            TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingRuleDetails.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingRulesConfigurationService, times(1))
        .processGetSourcingRuleDetailsByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingRuleByIdandOrgIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingRulesConfigurationService.processGetSourcingRuleDetailsByIdandOrgId(
            anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching sourcing rule by id"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingRulesConfigurationController.getSourcingRuleDetailsByIdandOrgId(
                    TestUtil.SOURCING_RULE_ID, TestUtil.ORG_ID));
    assertEquals("Error in fetching sourcing rule by id", ex.getMessage());

    verify(sourcingRulesConfigurationService, times(1))
        .processGetSourcingRuleDetailsByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void deleteSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    SourcingRuleDetails sourcingRuleDetails = testUtil.getSourcingRuleDetails();
    when(sourcingRulesConfigurationService.processDeleteSourcingRuleDetails(anyString(), anyLong()))
        .thenReturn(sourcingRuleDetails);

    ResponseEntity<BaseResponse<SourcingRuleDetails>> responseEntity =
        sourcingRulesConfigurationController.deleteSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingRuleDetails.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingRulesConfigurationService, times(1))
        .processDeleteSourcingRuleDetails(anyString(), anyLong());
  }

  @Test
  void deleteSourcingRuleExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(sourcingRulesConfigurationService.processDeleteSourcingRuleDetails(anyString(), anyLong()))
        .thenThrow(new RuntimeException("Error in deleting sourcing rule"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingRulesConfigurationController.deleteSourcingRule(
                    TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID));
    assertEquals("Error in deleting sourcing rule", ex.getMessage());

    verify(sourcingRulesConfigurationService, times(1))
        .processDeleteSourcingRuleDetails(anyString(), anyLong());
  }

  @Test
  void fetchSourcingRulesTest() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);
    FetchSourcingRulesResponse fetchSourcingRulesResponse = testUtil.fetchSourcingRulesResponse("");
    when(sourcingRulesConfigurationService.processGetSourcingRules(
            any(FetchSourcingRulesRequest.class)))
        .thenReturn(fetchSourcingRulesResponse);

    ResponseEntity<BaseResponse<FetchSourcingRulesResponse>> responseEntity =
        sourcingRulesConfigurationController.fetchSourcingRules(fetchSourcingRulesRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(1, responseEntity.getBody().getPayload().getSourcingRulesInfo().size());

    verify(sourcingRulesConfigurationService, times(1))
        .processGetSourcingRules(any(FetchSourcingRulesRequest.class));
  }

  @Test
  void fetchSourcingRulesExceptionTest() throws PromiseEngineException, CommonServiceException {
    SourcingAttributeValuesInfo sourcingAttributeValuesInfo = new SourcingAttributeValuesInfo();
    sourcingAttributeValuesInfo.setRequiredAttributesValue(TestUtil.SOURCING_RULE);
    FetchSourcingRulesRequest fetchSourcingRulesRequest =
        testUtil.fetchSourcingRulesRequest(sourcingAttributeValuesInfo);
    when(sourcingRulesConfigurationService.processGetSourcingRules(
            any(FetchSourcingRulesRequest.class)))
        .thenThrow(new RuntimeException("Error in fetching sourcing rules"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingRulesConfigurationController.fetchSourcingRules(fetchSourcingRulesRequest));

    assertEquals("Error in fetching sourcing rules", ex.getMessage());
    verify(sourcingRulesConfigurationService, times(1))
        .processGetSourcingRules(any(FetchSourcingRulesRequest.class));
  }
}
