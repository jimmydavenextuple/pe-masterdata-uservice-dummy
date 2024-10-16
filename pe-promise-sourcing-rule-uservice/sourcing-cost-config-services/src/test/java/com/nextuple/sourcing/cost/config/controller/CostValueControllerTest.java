/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.CostValueCacheKeyDto;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.sourcing.cost.config.service.CostValueService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("CostValueController Test Cases")
class CostValueControllerTest {

  @Mock private CostValueService costValueService;

  @InjectMocks private CostValueController costValueController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test creating a Cost Value")
  void createCostValue() throws CommonServiceException {

    when(costValueService.createCostValue(anyString(), any()))
        .thenReturn(testUtil.getCostValueResponse(Boolean.TRUE));
    ResponseEntity<BaseResponse<CostValueResponse>> response =
        costValueController.createCostValue(
            TestUtil.ORG_ID, testUtil.getCreateCostValueRequest(Boolean.TRUE));
    assertEquals(201, response.getStatusCodeValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getBody().getPayload().getCostValue());
    assertEquals(
        TestUtil.COST_FACTOR_COMBINATION_KEY,
        response.getBody().getPayload().getCostFactorCombinationKey());
    verify(costValueService, times(1)).createCostValue(anyString(), any());
  }

  @Test
  @DisplayName("Test retrieving a Cost Value")
  void getCostValue() throws CommonServiceException {
    when(costValueService.getCostValue(anyString(), anyLong()))
        .thenReturn(testUtil.getCostValueResponse(Boolean.TRUE));
    ResponseEntity<BaseResponse<CostValueResponse>> response =
        costValueController.getCostValue(TestUtil.ORG_ID, TestUtil.ID);
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getBody().getPayload().getCostValue());
    assertEquals(
        TestUtil.COST_FACTOR_COMBINATION_KEY,
        response.getBody().getPayload().getCostFactorCombinationKey());
    assertEquals(200, response.getStatusCodeValue());
    verify(costValueService, times(1)).getCostValue(eq(TestUtil.ORG_ID), eq(TestUtil.ID));
  }

  @Test
  @DisplayName("Test retrieving a Cost Value by orgId,costItinerary and costFactorCombinationKey")
  void getCostValueForCostFactorCombinationKey() throws CommonServiceException {
    when(costValueService.getCostValueForCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(testUtil.getCostValueResponse(Boolean.TRUE));
    ResponseEntity<BaseResponse<CostValueResponse>> response =
        costValueController.getCostValueForCostFactorCombinationKey(
            TestUtil.ORG_ID, TestUtil.COST_ITINERARY, TestUtil.COST_FACTOR_COMBINATION_KEY);
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getBody().getPayload().getCostValue());
    assertEquals(
        TestUtil.COST_FACTOR_COMBINATION_KEY,
        response.getBody().getPayload().getCostFactorCombinationKey());
    assertEquals(200, response.getStatusCodeValue());
    verify(costValueService, times(1))
        .getCostValueForCostFactorCombinationKey(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("Test updating a Cost Value")
  void updateCostValue() throws CommonServiceException {

    when(costValueService.updateCostValue(anyLong(), anyString(), any()))
        .thenReturn(testUtil.getCostValueResponse(Boolean.TRUE));
    ResponseEntity<BaseResponse<CostValueResponse>> response =
        costValueController.updateCostValue(
            TestUtil.ORG_ID, TestUtil.ID, testUtil.getUpdateCostValueRequest(Boolean.TRUE));
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.COST_ITINERARY, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getBody().getPayload().getCostValue());
    assertEquals(
        TestUtil.COST_FACTOR_COMBINATION_KEY,
        response.getBody().getPayload().getCostFactorCombinationKey());
    verify(costValueService, times(1))
        .updateCostValue(
            eq(TestUtil.ID),
            eq(TestUtil.ORG_ID),
            eq(testUtil.getUpdateCostValueRequest(Boolean.TRUE)));
  }

  @Test
  @DisplayName("Test deleting a Cost Value")
  void deleteCostValueTest() throws CommonServiceException {
    CostValueResponse costValueResponse = testUtil.getCostValueResponse(Boolean.TRUE);
    when(costValueService.deleteCostValue(anyString(), anyLong())).thenReturn(costValueResponse);
    ResponseEntity<BaseResponse<CostValueResponse>> response =
        costValueController.deleteCostValue(TestUtil.ORG_ID, TestUtil.ID);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(TestUtil.COST_ITINERARY, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_VALUE_WITH_PREV_SLB, response.getBody().getPayload().getCostValue());
    assertEquals(
        TestUtil.COST_FACTOR_COMBINATION_KEY,
        response.getBody().getPayload().getCostFactorCombinationKey());
    verify(costValueService, times(1)).deleteCostValue(anyString(), anyLong());
  }

  @Test
  void deleteCostValueCostFactorCombinationKey_shouldReturnOkResponse()
      throws CommonServiceException {
    // Arrange
    CostValueResponse costValueResponse = testUtil.getCostValueResponse(Boolean.TRUE);
    when(costValueService.deleteCostValueForCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(costValueResponse);

    // Act
    ResponseEntity<BaseResponse<CostValueResponse>> response =
        costValueController.deleteCostValueCostFactorCombinationKey(
            TestUtil.ORG_ID, TestUtil.COST_ITINERARY, TestUtil.COST_FACTOR_COMBINATION_KEY);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Cost value deleted successfully.", response.getBody().getMessage());

    verify(costValueService, times(1))
        .deleteCostValueForCostFactorCombinationKey(
            TestUtil.ORG_ID, TestUtil.COST_ITINERARY, TestUtil.COST_FACTOR_COMBINATION_KEY);
  }

  @Test
  void getCostFactorCacheKeysTest() {
    List<CostValueCacheKeyDto> costValueCacheKeyDtoList = testUtil.getCostValueCacheKeysDtoList();

    when(costValueService.getCostValueCacheKeys(any())).thenReturn(costValueCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostValueCacheKeyDto>>> response =
        costValueController.getCostValueCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(costValueCacheKeyDtoList, response.getBody().getPayload());

    verify(costValueService, times(1)).getCostValueCacheKeys(any());
  }
}
