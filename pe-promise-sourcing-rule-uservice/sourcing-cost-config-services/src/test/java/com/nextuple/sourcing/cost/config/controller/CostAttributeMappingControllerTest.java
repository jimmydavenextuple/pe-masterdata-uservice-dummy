/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

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
import com.nextuple.sourcing.cost.config.dto.CostAttributeMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeMappingRequest;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
import com.nextuple.sourcing.cost.config.service.CostAttributeMappingService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CostAttributeMappingControllerTest {
  @Mock private CostAttributeMappingService costAttributeMappingService;

  @InjectMocks private CostAttributeMappingController costAttributeMappingController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for creating a cost attribute mapping")
  void createCostAttributeMappingTest() throws CommonServiceException {
    CostAttributeMappingResponse costAttributeMappingResponse =
        testUtil.getCostAttributeMappingResponse();
    when(costAttributeMappingService.createCostAttributeMapping(
            anyString(), any(CostAttributeMappingRequest.class)))
        .thenReturn(costAttributeMappingResponse);

    ResponseEntity<BaseResponse<CostAttributeMappingResponse>> responseEntity =
        costAttributeMappingController.createCostAttributeMapping(
            ORG_ID, testUtil.getCostAttributeMappingRequest());

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(
        costAttributeMappingResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeMappingService, times(1))
        .createCostAttributeMapping(anyString(), any(CostAttributeMappingRequest.class));
  }

  @Test
  @DisplayName("Test for fetching cost attribute mapping details by orgId and id")
  void getCostAttributeMappingByOrgIdAndIdTest() throws CommonServiceException {
    CostAttributeMappingResponse costAttributeMappingResponse =
        testUtil.getCostAttributeMappingResponse();
    when(costAttributeMappingService.findCostAttributeMappingByOrgIdAndId(anyString(), anyLong()))
        .thenReturn(costAttributeMappingResponse);

    ResponseEntity<BaseResponse<CostAttributeMappingResponse>> responseEntity =
        costAttributeMappingController.getCostAttributeMappingByOrgIdAndId(ORG_ID, TestUtil.ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costAttributeMappingResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeMappingService, times(1))
        .findCostAttributeMappingByOrgIdAndId(anyString(), anyLong());
  }

  @Test
  @DisplayName("Test for updating cost attribute mapping")
  void updateCostAttributeMappingTest() throws CommonServiceException {
    CostAttributeMappingResponse costAttributeMappingResponse =
        testUtil.getCostAttributeMappingResponse();
    when(costAttributeMappingService.updateCostAttributeMapping(
            anyLong(), anyString(), any(CostAttributeMappingRequest.class)))
        .thenReturn(costAttributeMappingResponse);

    ResponseEntity<BaseResponse<CostAttributeMappingResponse>> responseEntity =
        costAttributeMappingController.updateCostAttributeMapping(
            ORG_ID, COST_FACTOR_ID, testUtil.getCostAttributeMappingRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costAttributeMappingResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeMappingService, times(1))
        .updateCostAttributeMapping(anyLong(), anyString(), any(CostAttributeMappingRequest.class));
  }

  @Test
  @DisplayName("Test for fetching cost attribute mapping details by orgId and canonicalName")
  void getCostAttributeMappingByOrgIdAndCanonicalNameTest() throws CommonServiceException {
    CostAttributeMappingResponse costAttributeMappingResponse =
        testUtil.getCostAttributeMappingResponse();
    when(costAttributeMappingService.findCostAttributeMappingByOrgIdAndCanonicalName(
            anyString(), anyString()))
        .thenReturn(costAttributeMappingResponse);

    ResponseEntity<BaseResponse<CostAttributeMappingResponse>> responseEntity =
        costAttributeMappingController.getCostAttributeMappingByOrgIdAndCanonicalName(
            ORG_ID, TestUtil.CANONICAL_NAME);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costAttributeMappingResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeMappingService, times(1))
        .findCostAttributeMappingByOrgIdAndCanonicalName(anyString(), anyString());
  }

  @Test
  void getCostAttributeMappingCacheKeysTest() {
    List<CostAttributeMappingCacheKeyDto> tenantCostTypeCacheKeyDtoList =
        testUtil.getCostAttributeMappingCacheKeys();
    when(costAttributeMappingService.getAllCostAttributeMappingCacheKeys(any()))
        .thenReturn(tenantCostTypeCacheKeyDtoList);
    ResponseEntity<BaseResponse<List<CostAttributeMappingCacheKeyDto>>> response =
        costAttributeMappingController.getCostAttributeMappingCacheKeys(2);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tenantCostTypeCacheKeyDtoList, response.getBody().getPayload());
    verify(costAttributeMappingService, times(1)).getAllCostAttributeMappingCacheKeys(any());
  }
}
