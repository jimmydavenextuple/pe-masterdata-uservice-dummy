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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.DetailedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NamedOptimizationStrategyResponse;
import com.nextuple.promise.sourcing.rule.service.NamedOptimizationStrategyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NamedOptimizationStrategyControllerTest {

  @Mock private NamedOptimizationStrategyService namedOptimizationStrategyService;
  @InjectMocks private NamedOptimizationStrategyController namedOptimizationStrategyController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addOptimizationStrategyTest() throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        testUtil.getNamedOptimizationStrategyResponse();
    when(namedOptimizationStrategyService.processAddOptimizationStrategy(
            any(NamedOptimizationStrategyRequest.class)))
        .thenReturn(namedOptimizationStrategyResponse);

    ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> responseEntity =
        namedOptimizationStrategyController.addOptimizationStrategy(
            testUtil.getNamedOptimizationStrategyRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        namedOptimizationStrategyResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(namedOptimizationStrategyService, times(1))
        .processAddOptimizationStrategy(any(NamedOptimizationStrategyRequest.class));
  }

  @Test
  void addOptimizationStrategyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.processAddOptimizationStrategy(
            any(NamedOptimizationStrategyRequest.class)))
        .thenThrow(new RuntimeException("Error in adding optimization strategy"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              namedOptimizationStrategyController.addOptimizationStrategy(
                  testUtil.getNamedOptimizationStrategyRequest());
            });
    assertEquals("Error in adding optimization strategy", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .processAddOptimizationStrategy(any(NamedOptimizationStrategyRequest.class));
  }

  @Test
  void getOptimizationStrategyByIdTest() throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        testUtil.getNamedOptimizationStrategyResponse();
    when(namedOptimizationStrategyService.processGetOptimizationStrategyByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(namedOptimizationStrategyResponse);

    ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> responseEntity =
        namedOptimizationStrategyController.getOptimizationStrategyByOrgIdAndId(
            TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        namedOptimizationStrategyResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(namedOptimizationStrategyService, times(1))
        .processGetOptimizationStrategyByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getOptimizationStrategyByIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.processGetOptimizationStrategyByIdAndOrgId(
            anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching optimization strategy by id"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              namedOptimizationStrategyController.getOptimizationStrategyByOrgIdAndId(
                  TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_ID);
            });
    assertEquals("Error in fetching optimization strategy by id", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .processGetOptimizationStrategyByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getOptimizationStrategyByOrgIdAndGroupIdTest()
      throws PromiseEngineException, CommonServiceException {
    DetailedOptimizationStrategyResponse detailedOptimizationStrategyResponse =
        testUtil.getOptimizationStrategyResponse();
    when(namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenReturn(detailedOptimizationStrategyResponse);

    ResponseEntity<BaseResponse<DetailedOptimizationStrategyResponse>> responseEntity =
        namedOptimizationStrategyController.getOptimizationStrategyByOrgIdAndGroupId(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        detailedOptimizationStrategyResponse.getId(),
        responseEntity.getBody().getPayload().getId());

    verify(namedOptimizationStrategyService, times(1))
        .processGetOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void getOptimizationStrategyByOrgIdAndGroupIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.processGetOptimizationStrategyByOrgIdAndGroupId(
            anyString(), anyString()))
        .thenThrow(
            new RuntimeException("Error in fetching optimization strategy by orgId and groupId"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              namedOptimizationStrategyController.getOptimizationStrategyByOrgIdAndGroupId(
                  TestUtil.ORG_ID, TestUtil.GROUP_ID);
            });
    assertEquals("Error in fetching optimization strategy by orgId and groupId", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .processGetOptimizationStrategyByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void updateOptimizationStrategyTest() throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        testUtil.getUpdatedNamedOptimizationStrategyResponse();

    when(namedOptimizationStrategyService.processUpdateOptimizationStrategy(
            anyString(), anyString(), any(NamedOptimizationStrategyUpdationRequest.class)))
        .thenReturn(namedOptimizationStrategyResponse);

    ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> responseEntity =
        namedOptimizationStrategyController.updateOptimizationStrategy(
            TestUtil.ORG_ID,
            TestUtil.GROUP_ID,
            testUtil.getNamedOptimizationStrategyUpdationRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        namedOptimizationStrategyResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(namedOptimizationStrategyService, times(1))
        .processUpdateOptimizationStrategy(
            anyString(), anyString(), any(NamedOptimizationStrategyUpdationRequest.class));
  }

  @Test
  void updateOptimizationStrategyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.processUpdateOptimizationStrategy(
            anyString(), anyString(), any(NamedOptimizationStrategyUpdationRequest.class)))
        .thenThrow(new RuntimeException("Error in updating optimization strategy"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              namedOptimizationStrategyController.updateOptimizationStrategy(
                  TestUtil.ORG_ID,
                  TestUtil.GROUP_ID,
                  testUtil.getNamedOptimizationStrategyUpdationRequest());
            });
    assertEquals("Error in updating optimization strategy", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .processUpdateOptimizationStrategy(
            anyString(), anyString(), any(NamedOptimizationStrategyUpdationRequest.class));
  }

  @Test
  void deleteOptimizationStrategyTest() throws PromiseEngineException, CommonServiceException {
    NamedOptimizationStrategyResponse namedOptimizationStrategyResponse =
        testUtil.getNamedOptimizationStrategyResponse();
    when(namedOptimizationStrategyService.processDeleteOptimizationStrategy(
            anyString(), anyString()))
        .thenReturn(namedOptimizationStrategyResponse);

    ResponseEntity<BaseResponse<NamedOptimizationStrategyResponse>> responseEntity =
        namedOptimizationStrategyController.deleteOptimizationStrategy(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        namedOptimizationStrategyResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(namedOptimizationStrategyService, times(1))
        .processDeleteOptimizationStrategy(anyString(), anyString());
  }

  @Test
  void deleteOptimizationStrategyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.processDeleteOptimizationStrategy(
            anyString(), anyString()))
        .thenThrow(new RuntimeException("Error in deleting optimization strategy"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              namedOptimizationStrategyController.deleteOptimizationStrategy(
                  TestUtil.ORG_ID, TestUtil.GROUP_ID);
            });
    assertEquals("Error in deleting optimization strategy", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .processDeleteOptimizationStrategy(anyString(), anyString());
  }
}
