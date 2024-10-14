/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingRuleIdRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AllSourcingRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.NodeGroupInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.SourcingRuleDetails;
import com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class SourcingRulesConfigurationUIControllerTest {
  @Mock SourcingRulesConfigurationService sourcingRulesConfigurationService;
  @InjectMocks TestUtil testUtil;
  @InjectMocks SourcingRulesConfigurationUIController sourcingRulesConfigurationUIController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAllSourcingRuleByOrgIdTest() throws PromiseEngineException, CommonServiceException {
    AllSourcingRulesResponse allSourcingRulesResponse = getAllSourcingRulesResponse();
    Mockito.when(
            sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(
                TestUtil.ORG_ID))
        .thenReturn(List.of(allSourcingRulesResponse));

    var response =
        sourcingRulesConfigurationUIController.getAllSourcingRuleByOrgId(
            TestUtil.ORG_ID, TestUtil.PAGE_NO, TestUtil.PAGE_SIZE);
    Assertions.assertEquals(
        "All sourcing rule details fetched successfully", response.getBody().getMessage());
    Assertions.assertNotNull(response.getBody().getPayload());
  }

  @Test
  void getAllSourcingRuleByOrgIdUnPagedTest()
      throws PromiseEngineException, CommonServiceException {
    AllSourcingRulesResponse allSourcingRulesResponse = getAllSourcingRulesResponse();
    Mockito.when(
            sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(
                TestUtil.ORG_ID))
        .thenReturn(List.of(allSourcingRulesResponse));

    var response =
        sourcingRulesConfigurationUIController.getAllSourcingRuleByOrgId(
            TestUtil.ORG_ID, TestUtil.PAGE_NO, null);
    Assertions.assertEquals(
        "All sourcing rule details fetched successfully", response.getBody().getMessage());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertEquals(1, response.getBody().getPayload().getPagination().getTotalPages());
  }

  private AllSourcingRulesResponse getAllSourcingRulesResponse() {
    return AllSourcingRulesResponse.builder()
        .sourcingRule("V1:V2")
        .orgId(TestUtil.ORG_ID)
        .sourcingRuleName("Rule 1")
        .sourcingAttributesDefinitionId(1L)
        .optionalAttributes(
            List.of(
                AttributeInfo.builder()
                    .attributeName("opt1")
                    .attributeValue("optval1")
                    .attributeId("optid1")
                    .build()))
        .requiredAttributes(
            List.of(
                AttributeInfo.builder()
                    .attributeName("req1")
                    .attributeValue("reqval1")
                    .attributeId("reqd1")
                    .build()))
        .nodes(
            List.of(
                NodeGroupInfo.builder()
                    .nodeGroupId(1L)
                    .nodeGroupName("NG NAME")
                    .sequence(1)
                    .numberOfNodes(1)
                    .build()))
        .build();
  }

  @Test
  void getAllSourcingRuleByOrgIdTestException()
      throws PromiseEngineException, CommonServiceException {

    when(sourcingRulesConfigurationService.processGetAllSourcingRuleDetailsByOrgId(TestUtil.ORG_ID))
        .thenThrow(new RuntimeException());

    Assertions.assertThrows(
        Exception.class,
        () ->
            sourcingRulesConfigurationUIController.getAllSourcingRuleByOrgId(
                TestUtil.ORG_ID, 1, 2));
  }

  @Test
  void deleteMultipleSourcingRulesTest() throws PromiseEngineException, CommonServiceException {
    SourcingRuleIdRequest sourcingRuleIdRequest =
        SourcingRuleIdRequest.builder()
            .sourcingRuleIds(List.of((TestUtil.SOURCING_RULE_ID)))
            .build();

    List<SourcingRuleDetails> sourcingRuleDetails = List.of(testUtil.getSourcingRuleDetails());

    when(sourcingRulesConfigurationService.processDeleteMultipleSourcingRuleDetails(any(), any()))
        .thenReturn(sourcingRuleDetails);

    var response =
        sourcingRulesConfigurationUIController.deleteMultipleSourcingRules(
            TestUtil.ORG_ID, sourcingRuleIdRequest);

    Assertions.assertEquals("Sourcing rule deleted successfully", response.getBody().getMessage());
    Assertions.assertEquals(sourcingRuleDetails, response.getBody().getPayload());
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void deleteMultipleSourcingRulesExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingRuleIdRequest sourcingRuleIdRequest =
        SourcingRuleIdRequest.builder().sourcingRuleIds(List.of(TestUtil.SOURCING_RULE_ID)).build();

    List<SourcingRuleDetails> sourcingRuleDetails = List.of(testUtil.getSourcingRuleDetails());

    when(sourcingRulesConfigurationService.processDeleteMultipleSourcingRuleDetails(any(), any()))
        .thenThrow(new CommonServiceException(HttpStatus.BAD_REQUEST, 400, null));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                sourcingRulesConfigurationUIController.deleteMultipleSourcingRules(
                    TestUtil.ORG_ID, sourcingRuleIdRequest));
    Assertions.assertEquals(CommonServiceException.class, ex.getClass());
  }

  @Test
  void createSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    AllSourcingRulesResponse allSourcingRulesResponse =
        AllSourcingRulesResponse.builder()
            .sourcingRule("V1:V2")
            .orgId(TestUtil.ORG_ID)
            .sourcingRuleName("Rule 1")
            .sourcingAttributesDefinitionId(1L)
            .optionalAttributes(
                List.of(
                    AttributeInfo.builder()
                        .attributeName("opt1")
                        .attributeValue("optval1")
                        .attributeId("optid1")
                        .build()))
            .requiredAttributes(
                List.of(
                    AttributeInfo.builder()
                        .attributeName("req1")
                        .attributeValue("reqval1")
                        .attributeId("reqd1")
                        .build()))
            .nodes(
                List.of(
                    NodeGroupInfo.builder()
                        .nodeGroupId(1L)
                        .nodeGroupName("NG NAME")
                        .sequence(1)
                        .numberOfNodes(1)
                        .build()))
            .build();
    when(sourcingRulesConfigurationService.createSourcingRule(any(), any()))
        .thenReturn(allSourcingRulesResponse);
    var response =
        sourcingRulesConfigurationUIController.createSourcingRule(
            TestUtil.ORG_ID, allSourcingRulesResponse);
    Assertions.assertEquals("Sourcing rule created successfully", response.getBody().getMessage());
    Assertions.assertNotNull(response.getBody().getPayload());
  }

  @Test
  void createSourcingRuleTestException() throws PromiseEngineException, CommonServiceException {
    AllSourcingRulesResponse response = new AllSourcingRulesResponse();
    when(sourcingRulesConfigurationService.createSourcingRule(any(), any()))
        .thenThrow(new RuntimeException());

    Assertions.assertThrows(
        RuntimeException.class,
        () -> sourcingRulesConfigurationUIController.createSourcingRule(TestUtil.ORG_ID, response));
  }

  @Test
  void getSourcingRuleByOrgIdAndSourcingRuleTest()
      throws PromiseEngineException, CommonServiceException {

    AllSourcingRulesResponse allSourcingRulesResponse =
        AllSourcingRulesResponse.builder()
            .sourcingRule("V1:V2")
            .orgId(TestUtil.ORG_ID)
            .sourcingRuleName("Rule 1")
            .sourcingAttributesDefinitionId(1L)
            .optionalAttributes(
                List.of(
                    AttributeInfo.builder()
                        .attributeName("opt1")
                        .attributeValue("optval1")
                        .attributeId("optid1")
                        .build()))
            .requiredAttributes(
                List.of(
                    AttributeInfo.builder()
                        .attributeName("req1")
                        .attributeValue("reqval1")
                        .attributeId("reqd1")
                        .build()))
            .nodes(
                List.of(
                    NodeGroupInfo.builder()
                        .nodeGroupId(1L)
                        .nodeGroupName("NG NAME")
                        .sequence(1)
                        .numberOfNodes(1)
                        .build()))
            .build();

    when(sourcingRulesConfigurationService.processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID))
        .thenReturn(allSourcingRulesResponse);

    var response =
        sourcingRulesConfigurationUIController.getSourcingRuleByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID);

    Assertions.assertEquals("Sourcing rule fetched successfully", response.getBody().getMessage());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertEquals(allSourcingRulesResponse, response.getBody().getPayload());
  }

  @Test
  void getSourcingRuleByOrgIdAndSourcingRuleTestException()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingRulesConfigurationService.processGetSourcingRuleDetailsByOrgIdAndSourcingRule(
            TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID))
        .thenThrow(new RuntimeException());

    Assertions.assertThrows(
        Exception.class,
        () ->
            sourcingRulesConfigurationUIController.getSourcingRuleByOrgIdAndSourcingRule(
                TestUtil.ORG_ID, TestUtil.SOURCING_RULE_ID));
  }
}
