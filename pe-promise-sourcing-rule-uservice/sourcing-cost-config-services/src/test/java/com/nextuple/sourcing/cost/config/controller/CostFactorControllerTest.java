/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorRequest;
import com.nextuple.sourcing.cost.config.inbound.CostFactorUpdateRequest;
import com.nextuple.sourcing.cost.config.service.CostFactorService;
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

class CostFactorControllerTest {
  @Mock private CostFactorService costFactorService;

  @InjectMocks private CostFactorController costFactorController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCostFactorTest() throws CommonServiceException {
    CostFactorDto costFactorResponse = testUtil.getCostFactorResponse();
    when(costFactorService.createCostFactor(anyString(), any(CostFactorRequest.class)))
        .thenReturn(costFactorResponse);

    ResponseEntity<BaseResponse<CostFactorDto>> responseEntity =
        costFactorController.createCostFactor(ORG_ID, testUtil.getCreateCostFactorRequest());

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(costFactorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costFactorService, times(1)).createCostFactor(anyString(), any(CostFactorRequest.class));
  }

  @Test
  void getCostFactorByOrgIdAndIdTest() throws CommonServiceException {
    CostFactorDto costFactorResponse = testUtil.getCostFactorResponse();
    when(costFactorService.findCostFactorByOrgIdAndCostFactorId(anyString(), anyLong()))
        .thenReturn(costFactorResponse);

    ResponseEntity<BaseResponse<CostFactorDto>> responseEntity =
        costFactorController.getCostFactorByOrgIdAndCostFactorId(ORG_ID, COST_FACTOR_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costFactorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costFactorService, times(1))
        .findCostFactorByOrgIdAndCostFactorId(anyString(), anyLong());
  }

  @Test
  @DisplayName("Get cost factor by org id and cost factor")
  void getCostFactorByOrgIdAndCostFactorTest() throws CommonServiceException {
    CostFactorDto costFactorResponse = testUtil.getCostFactorResponse();
    when(costFactorService.findCostFactorByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(costFactorResponse);

    ResponseEntity<BaseResponse<CostFactorDto>> responseEntity =
        costFactorController.getCostFactorByOrgIdAndCostFactor(ORG_ID, COST_FACTOR);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costFactorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costFactorService, times(1))
        .findCostFactorByOrgIdAndCostFactor(anyString(), anyString());
  }

  @Test
  void updatePreferenceSelectorTest() throws CommonServiceException {
    CostFactorDto costFactorResponse = testUtil.getCostFactorResponse();
    when(costFactorService.updateCostFactor(
            anyLong(), anyString(), any(CostFactorUpdateRequest.class)))
        .thenReturn(costFactorResponse);

    ResponseEntity<BaseResponse<CostFactorDto>> responseEntity =
        costFactorController.updateCostFactor(
            ORG_ID, COST_FACTOR_ID, testUtil.getCostFactorUpdationRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costFactorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costFactorService, times(1))
        .updateCostFactor(anyLong(), anyString(), any(CostFactorUpdateRequest.class));
  }

  @Test
  void deletePreferenceSelectorTest() throws CommonServiceException {
    CostFactorDto costFactorResponse = testUtil.getCostFactorResponse();
    when(costFactorService.deleteCostFactor(anyLong(), anyString())).thenReturn(costFactorResponse);

    ResponseEntity<BaseResponse<CostFactorDto>> responseEntity =
        costFactorController.deleteCostFactor(ORG_ID, COST_FACTOR_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costFactorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costFactorService, times(1)).deleteCostFactor(anyLong(), anyString());
  }

  @Test
  void getCostFactorCacheKeysTest() {
    List<CostFactorCacheKeyDto> costFactorCacheKeyDtoList =
        testUtil.getCostFactorCacheKeysDtoList();

    when(costFactorService.getCostFactorCacheKeys(any())).thenReturn(costFactorCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostFactorCacheKeyDto>>> response =
        costFactorController.getCostFactorCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(costFactorCacheKeyDtoList, response.getBody().getPayload());

    verify(costFactorService, times(1)).getCostFactorCacheKeys(any());
  }
}
