/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.OPT_STRATEGY;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.inbound.CreateOptimizationAndCostTypesMappingRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateOptimizationAndCostTypesMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import com.nextuple.sourcing.cost.config.service.OptimizationAndCostTypesMappingService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@DisplayName("OptimizationAndCostTypesMappingController Test Cases")
class OptimizationAndCostTypesMappingControllerTest {

  @Mock private OptimizationAndCostTypesMappingService optimizationAndCostTypesMappingService;

  @InjectMocks
  private OptimizationAndCostTypesMappingController optimizationAndCostTypesMappingController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("Create Optimization And Cost Types Mapping Service")
  @Test
  void createOptimizationAndCostTypesMappingTest()
      throws CommonServiceException, PromiseEngineException {
    var request = testUtil.getCreateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingService.createOptimizationAndCostTypesMapping(
            any(CreateOptimizationAndCostTypesMappingRequest.class)))
        .thenReturn(testUtil.getOptimizationAndCostTypesMappingResponse());

    ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>> response =
        optimizationAndCostTypesMappingController.createOptimizationAndCostTypesMapping(
            testUtil.getCreateOptimizationAndCostTypesMappingRequest());

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
    assertEquals(request.getOrgId(), response.getBody().getPayload().getOrgId());
    assertEquals(request.getCostTypes(), response.getBody().getPayload().getCostTypes());
    assertEquals(request.getDescription(), response.getBody().getPayload().getDescription());
    verify(optimizationAndCostTypesMappingService, times(1))
        .createOptimizationAndCostTypesMapping(
            any(CreateOptimizationAndCostTypesMappingRequest.class));
  }

  @DisplayName("Update Optimization And Cost Types Mapping Service By Id And Org Id")
  @Test
  void updateOptimizationAndCostTypesMappingByIdAndOrgIdTest() throws CommonServiceException {
    var request = testUtil.getUpdateOptimizationAndCostTypesMappingRequest();

    when(optimizationAndCostTypesMappingService.updateOptimizationAndCostTypesMappingByIdAndOrgId(
            anyLong(), anyString(), any(UpdateOptimizationAndCostTypesMappingRequest.class)))
        .thenReturn(testUtil.getOptimizationAndCostTypesMappingResponse());

    ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>> response =
        optimizationAndCostTypesMappingController.updateOptimizationAndCostTypesMappingByIdAndOrgId(
            ORG_ID, ID, testUtil.getUpdateOptimizationAndCostTypesMappingRequest());

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(request.getJavaClassName(), response.getBody().getPayload().getJavaClassName());
    assertEquals(request.getCostTypes(), response.getBody().getPayload().getCostTypes());
    assertEquals(request.getDescription(), response.getBody().getPayload().getDescription());
    assertEquals(
        request.getCustomAttributes(), response.getBody().getPayload().getCustomAttributes());
    verify(optimizationAndCostTypesMappingService, times(1))
        .updateOptimizationAndCostTypesMappingByIdAndOrgId(
            anyLong(), anyString(), any(UpdateOptimizationAndCostTypesMappingRequest.class));
  }

  @DisplayName(
      "Fetch Optimization And Cost Types Mapping Service By Org Id And Optimization Strategy")
  @Test
  void fetchOptimizationAndCostTypesMappingByOrgIdAndStrategyTest() throws CommonServiceException {
    when(optimizationAndCostTypesMappingService
            .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(anyString(), anyString()))
        .thenReturn(testUtil.getOptimizationAndCostTypesMappingResponse());

    ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>> response =
        optimizationAndCostTypesMappingController
            .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(ORG_ID, OPT_STRATEGY);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(OPT_STRATEGY, response.getBody().getPayload().getOptimizationStrategy());
    verify(optimizationAndCostTypesMappingService, times(1))
        .fetchOptimizationAndCostTypesMappingByOrgIdAndStrategy(anyString(), anyString());
  }

  @DisplayName("Fetch Optimization And Cost Types Mapping Service By Org Id And Id")
  @Test
  void fetchOptimizationAndCostTypesMappingByOrgIdAndIdTest() throws CommonServiceException {
    when(optimizationAndCostTypesMappingService.fetchOptimizationAndCostTypesMappingByOrgIdAndId(
            anyString(), anyLong()))
        .thenReturn(testUtil.getOptimizationAndCostTypesMappingResponse());

    ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>> response =
        optimizationAndCostTypesMappingController.fetchOptimizationAndCostTypesMappingByOrgIdAndId(
            ORG_ID, ID);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(ID, response.getBody().getPayload().getId());
    verify(optimizationAndCostTypesMappingService, times(1))
        .fetchOptimizationAndCostTypesMappingByOrgIdAndId(anyString(), anyLong());
  }

  @DisplayName("Delete Optimization And Cost Types Mapping Service By Org Id And Id")
  @Test
  void deleteOptimizationAndCostTypesMappingByOrgIdAndIdTest() throws CommonServiceException {
    when(optimizationAndCostTypesMappingService.deleteOptimizationAndCostTypesMappingByOrgIdAndId(
            anyString(), anyLong()))
        .thenReturn(testUtil.getOptimizationAndCostTypesMappingResponse());

    ResponseEntity<BaseResponse<OptimizationAndCostTypesMappingResponse>> response =
        optimizationAndCostTypesMappingController.deleteOptimizationAndCostTypesMappingByOrgIdAndId(
            ORG_ID, ID);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(ID, response.getBody().getPayload().getId());
    verify(optimizationAndCostTypesMappingService, times(1))
        .deleteOptimizationAndCostTypesMappingByOrgIdAndId(anyString(), anyLong());
  }
}
