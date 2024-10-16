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
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.dto.AllConstraintUIDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteOptimizationRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.OptimizationStrategyUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.OptimizationRuleUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.service.NamedOptimizationStrategyService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OptimizationRulesDashboardControllerTest {
  @Mock private NamedOptimizationStrategyService namedOptimizationStrategyService;
  @InjectMocks private OptimizationRulesDashboardController optimizationRulesDashboardController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createOptimizationRuleTest() throws PromiseEngineException, CommonServiceException {

    OptimizationRuleUIResponse response = testUtil.getOptimizationStrategyUIResponse();

    when(namedOptimizationStrategyService.createOptimizationRuleUI(
            anyString(), any(OptimizationStrategyUIRequest.class)))
        .thenReturn(response);

    ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> responseEntity =
        optimizationRulesDashboardController.createOptimizationRule(
            TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        response.getOptimizationRuleId(),
        responseEntity.getBody().getPayload().getOptimizationRuleId());
    verify(namedOptimizationStrategyService, times(1))
        .createOptimizationRuleUI(anyString(), any(OptimizationStrategyUIRequest.class));
  }

  @Test
  void createOptimizationRuleExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.createOptimizationRuleUI(
            anyString(), any(OptimizationStrategyUIRequest.class)))
        .thenThrow(new RuntimeException("Failed to create Optimization Rule"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              optimizationRulesDashboardController.createOptimizationRule(
                  TestUtil.ORG_ID, testUtil.getOptimizationStrategyUIRequest());
            });
    assertEquals("Failed to create Optimization Rule", ex.getMessage());
    verify(namedOptimizationStrategyService, times(1))
        .createOptimizationRuleUI(anyString(), any(OptimizationStrategyUIRequest.class));
  }

  @Test
  void deleteMultipleOptimizationStrategyTest()
      throws PromiseEngineException, CommonServiceException {
    DeleteOptimizationRulesRequest optimizationRuleIds =
        new DeleteOptimizationRulesRequest(List.of(TestUtil.OPTIMIZATION_STRATEGY_ID));

    OptimizationRuleUIResponse optimizationStrategyUIResponse =
        testUtil.getOptimizationStrategyUIResponse();
    when(namedOptimizationStrategyService.processDeleteMultipleOptimizationStrategy(
            anyString(), any()))
        .thenReturn(List.of(optimizationStrategyUIResponse));

    ResponseEntity<BaseResponse<List<OptimizationRuleUIResponse>>> responseEntity =
        optimizationRulesDashboardController.deleteMultipleOptimizationStrategy(
            TestUtil.ORG_ID, optimizationRuleIds);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        optimizationStrategyUIResponse.getOptimizationRuleId(),
        responseEntity.getBody().getPayload().get(0).getOptimizationRuleId());

    verify(namedOptimizationStrategyService, times(1))
        .processDeleteMultipleOptimizationStrategy(anyString(), any());
  }

  @Test
  void deleteMultipleOptimizationStrategyExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    DeleteOptimizationRulesRequest optimizationRuleIds =
        new DeleteOptimizationRulesRequest(List.of(TestUtil.OPTIMIZATION_STRATEGY_ID));
    when(namedOptimizationStrategyService.processDeleteMultipleOptimizationStrategy(
            anyString(), any()))
        .thenThrow(new RuntimeException("Failed to delete multiple optimization strategy request"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              optimizationRulesDashboardController.deleteMultipleOptimizationStrategy(
                  TestUtil.ORG_ID, optimizationRuleIds);
            });
    assertEquals("Failed to delete multiple optimization strategy request", ex.getMessage());
    verify(namedOptimizationStrategyService, times(1))
        .processDeleteMultipleOptimizationStrategy(anyString(), any());
  }

  @Test
  void editOptimizationRuleTest() throws PromiseEngineException, CommonServiceException {
    OptimizationRuleUIResponse optimizationRuleUIResponse =
        testUtil.getUpdatedOptimizationStrategyUIResponse();
    when(namedOptimizationStrategyService.processEditOptimizationRuleUI(
            anyString(), anyLong(), any()))
        .thenReturn(optimizationRuleUIResponse);

    ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> responseEntity =
        optimizationRulesDashboardController.editOptimizationRule(
            TestUtil.ORG_ID,
            TestUtil.OPTIMIZATION_STRATEGY_ID,
            testUtil.getOptimizationRuleUpdationUIRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        optimizationRuleUIResponse.getOptimizationRuleId(),
        responseEntity.getBody().getPayload().getOptimizationRuleId());

    verify(namedOptimizationStrategyService, times(1))
        .processEditOptimizationRuleUI(anyString(), anyLong(), any());
  }

  @Test
  void editOptimizationRuleExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(namedOptimizationStrategyService.processEditOptimizationRuleUI(
            anyString(), anyLong(), any()))
        .thenThrow(new RuntimeException("Failed to process edit optimization rule request"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              optimizationRulesDashboardController.editOptimizationRule(
                  TestUtil.ORG_ID,
                  TestUtil.OPTIMIZATION_STRATEGY_ID,
                  testUtil.getOptimizationRuleUpdationUIRequest());
            });
    assertEquals("Failed to process edit optimization rule request", ex.getMessage());
    verify(namedOptimizationStrategyService, times(1))
        .processEditOptimizationRuleUI(anyString(), anyLong(), any());
  }

  @Test
  void getOptimizationRuleByOrgIdAndNamedOptimizationStrategyIdHappyPathTest()
      throws PromiseEngineException, CommonServiceException {
    OptimizationRuleUIResponse optimizationRuleUIResponse =
        testUtil.getOptimizationRuleUIResponse("EXPRESS");

    when(namedOptimizationStrategyService.getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
            anyString(), anyLong()))
        .thenReturn(optimizationRuleUIResponse);

    ResponseEntity<BaseResponse<OptimizationRuleUIResponse>> responseEntity =
        optimizationRulesDashboardController
            .getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
                TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    assertEquals(
        optimizationRuleUIResponse.getOptimizationRuleId(),
        responseEntity.getBody().getPayload().getOptimizationRuleId());

    verify(namedOptimizationStrategyService, times(1))
        .getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(anyString(), anyLong());
  }

  @Test
  void getOptimizationRuleByOrgIdAndNamedOptimizationStrategyIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {

    when(namedOptimizationStrategyService.getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
            anyString(), anyLong()))
        .thenThrow(
            new RuntimeException(
                "Error in fetching optimization rule by orgId and optimizationRuleId"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              optimizationRulesDashboardController
                  .getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(
                      TestUtil.ORG_ID, TestUtil.OPTIMIZATION_STRATEGY_ID);
            });
    assertEquals(
        "Error in fetching optimization rule by orgId and optimizationRuleId", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .getOptimizationRuleByOrgIdAndNamedOptimizationStrategyId(anyString(), anyLong());
  }

  @Test
  void getAllUIConstraintsHappyPathTest() {
    List<AllConstraintUIDto> allConstraintUIDtoList = testUtil.getAllConstraintUIResponse();

    when(namedOptimizationStrategyService.getAllUIConstraints()).thenReturn(allConstraintUIDtoList);

    ResponseEntity<BaseResponse<List<AllConstraintUIDto>>> responseEntity =
        optimizationRulesDashboardController.getAllUIConstraints();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    assertEquals(
        allConstraintUIDtoList.get(0).getLabel(),
        responseEntity.getBody().getPayload().get(0).getLabel());

    verify(namedOptimizationStrategyService, times(1)).getAllUIConstraints();
  }

  @Test
  void getAllUIConstraintsExceptionTest() {

    when(namedOptimizationStrategyService.getAllUIConstraints())
        .thenThrow(new RuntimeException("Error in fetching All constraints"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              optimizationRulesDashboardController.getAllUIConstraints();
            });
    assertEquals("Error in fetching All constraints", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1)).getAllUIConstraints();
  }

  @Test
  void getOptimizationRulesByOrgIdHappyPathTest()
      throws PromiseEngineException, CommonServiceException {
    PageResponse<OptimizationRuleUIResponse> responsePageResponse =
        testUtil.getOptimizationRulesPageResponse();

    when(namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(
            anyString(), any(PageParams.class)))
        .thenReturn(responsePageResponse);

    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    ResponseEntity<BaseResponse<PageResponse<OptimizationRuleUIResponse>>> response =
        optimizationRulesDashboardController.getAllOptimizationRulesByOrgId(
            TestUtil.ORG_ID, pageParams);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertEquals(
        responsePageResponse.getData().get(0).getOptimizationRuleId(),
        response.getBody().getPayload().getData().get(0).getOptimizationRuleId());

    verify(namedOptimizationStrategyService, times(1))
        .getAllOptimizationRulesByOrgId(anyString(), any(PageParams.class));
  }

  @Test
  void getOptimizationRuleByOrgIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {

    when(namedOptimizationStrategyService.getAllOptimizationRulesByOrgId(
            anyString(), any(PageParams.class)))
        .thenThrow(new RuntimeException("Error in fetching optimization rules by orgId"));

    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              optimizationRulesDashboardController.getAllOptimizationRulesByOrgId(
                  TestUtil.ORG_ID, pageParams);
            });
    assertEquals("Error in fetching optimization rules by orgId", ex.getMessage());

    verify(namedOptimizationStrategyService, times(1))
        .getAllOptimizationRulesByOrgId(anyString(), any(PageParams.class));
  }
}
