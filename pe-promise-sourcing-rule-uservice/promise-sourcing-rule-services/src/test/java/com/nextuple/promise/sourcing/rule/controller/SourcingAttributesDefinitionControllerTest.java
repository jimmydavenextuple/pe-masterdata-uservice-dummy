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
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SourcingAttributesDefinitionControllerTest {

  @Mock private SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @InjectMocks
  private SourcingAttributesDefinitionController sourcingAttributesDefinitionController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createSourcingAttributesDefinitionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
            any(SourcingAttributesDefinitionRequest.class)))
        .thenReturn(sourcingRuleAttributesDefinitionResponse);

    ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>> responseEntity =
        sourcingAttributesDefinitionController.createSourcingAttributesDefinition(
            testUtil.getSourcingRuleAttributesDefinitionRequest(
                SourcingAttributesDefinitionStatus.ACTIVE));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        sourcingRuleAttributesDefinitionResponse.getId(),
        responseEntity.getBody().getPayload().getId());

    verify(sourcingAttributesDefinitionService, times(1))
        .processCreateSourcingAttributesDefinition(any(SourcingAttributesDefinitionRequest.class));
  }

  @Test
  void createSourcingAttributesDefinitionExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
            any(SourcingAttributesDefinitionRequest.class)))
        .thenThrow(new RuntimeException("Error in creating sourcing rule attributes definition"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingAttributesDefinitionController.createSourcingAttributesDefinition(
                    testUtil.getSourcingRuleAttributesDefinitionRequest(
                        SourcingAttributesDefinitionStatus.ACTIVE)));
    assertEquals("Error in creating sourcing rule attributes definition", ex.getMessage());

    verify(sourcingAttributesDefinitionService, times(1))
        .processCreateSourcingAttributesDefinition(any(SourcingAttributesDefinitionRequest.class));
  }

  @Test
  void getSourcingAttributesDefinitionByIdTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingRuleAttributesDefinitionResponse);

    ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>> responseEntity =
        sourcingAttributesDefinitionController.getSourcingAttributesDefinitionByIdandOrgId(
            TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID, TestUtil.ORG_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        sourcingRuleAttributesDefinitionResponse.getId(),
        responseEntity.getBody().getPayload().getId());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributesDefinitionByIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenThrow(
            new RuntimeException("Error in fetching sourcing rules attribute definition by id"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionController.getSourcingAttributesDefinitionByIdandOrgId(
                  TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID, TestUtil.ORG_ID);
            });

    assertEquals("Error in fetching sourcing rules attribute definition by id", ex.getMessage());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributesDefinitionInActiveStatusTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenReturn(sourcingRuleAttributesDefinitionResponse);

    ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>> responseEntity =
        sourcingAttributesDefinitionController.getSourcingAttributesDefinitionInActiveStatus(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        sourcingRuleAttributesDefinitionResponse.getId(),
        responseEntity.getBody().getPayload().getId());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionInActiveStatus(anyString(), any());
  }

  @Test
  void getSourcingAttributesDefinitionInActiveStatusExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            anyString(), any()))
        .thenThrow(
            new RuntimeException(
                "Error in fetching sourcing rules attribute definition in active status"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionController.getSourcingAttributesDefinitionInActiveStatus(
                  TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
            });

    assertEquals(
        "Error in fetching sourcing rules attribute definition in active status", ex.getMessage());
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionInActiveStatus(anyString(), any());
  }

  @Test
  void updateSourcingAttributesDefinitionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        testUtil.getUpdatedSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.INACTIVE);
    when(sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
            (anyLong()), (anyString()), any(SourcingAttributesDefinitionUpdationRequest.class)))
        .thenReturn(sourcingRuleAttributesDefinitionResponse);

    ResponseEntity<BaseResponse<SourcingAttributesDefinitionResponse>> responseEntity =
        sourcingAttributesDefinitionController.updateSourcingAttributesDefinition(
            TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
            TestUtil.ORG_ID,
            testUtil.getSourcingRuleAttributesDefinitionUpdationRequest(
                SourcingAttributesDefinitionStatus.INACTIVE));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        sourcingRuleAttributesDefinitionResponse.getId(),
        responseEntity.getBody().getPayload().getId());
    verify(sourcingAttributesDefinitionService, times(1))
        .updateSourcingAttributesDefinition(
            anyLong(), (anyString()), any(SourcingAttributesDefinitionUpdationRequest.class));
  }

  @Test
  void updateSourcingAttributesDefinitionExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
            (anyLong()), (anyString()), any(SourcingAttributesDefinitionUpdationRequest.class)))
        .thenThrow(new RuntimeException("Error in updating sourcing rules attribute definition"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionController.updateSourcingAttributesDefinition(
                  TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                  TestUtil.ORG_ID,
                  testUtil.getSourcingRuleAttributesDefinitionUpdationRequest(
                      SourcingAttributesDefinitionStatus.INACTIVE));
            });

    assertEquals("Error in updating sourcing rules attribute definition", ex.getMessage());
    verify(sourcingAttributesDefinitionService, times(1))
        .updateSourcingAttributesDefinition(
            anyLong(), (anyString()), any(SourcingAttributesDefinitionUpdationRequest.class));
  }
}
