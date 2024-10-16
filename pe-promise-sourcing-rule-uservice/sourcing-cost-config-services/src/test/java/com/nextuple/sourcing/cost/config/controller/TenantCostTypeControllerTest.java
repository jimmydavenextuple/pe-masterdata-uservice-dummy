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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.TenantCostTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.enums.LabelEnum;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import com.nextuple.sourcing.cost.config.service.TenantCostTypeService;
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

@DisplayName("TenantCostTypeController Test Cases")
class TenantCostTypeControllerTest {

  @Mock private TenantCostTypeService tenantCostTypeService;

  @InjectMocks private TenantCostTypeController tenantCostTypeController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test creating a Tenant Cost Type")
  void createTenantCostType() throws CommonServiceException {

    when(tenantCostTypeService.createTenantCostType(anyString(), any()))
        .thenReturn(testUtil.getTenantCostTypeResponse());
    ResponseEntity<BaseResponse<TenantCostTypeResponse>> response =
        tenantCostTypeController.createTenantCostType(
            TestUtil.ORG_ID, testUtil.getTenantCostTypeRequest());
    assertEquals(201, response.getStatusCodeValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(LabelEnum.COST, response.getBody().getPayload().getLabel());
    verify(tenantCostTypeService, times(1)).createTenantCostType(anyString(), any());
  }

  @Test
  @DisplayName("Test retrieving a Tenant Cost Type")
  void getTenantCostType() throws CommonServiceException {
    when(tenantCostTypeService.getTenantCostType(anyString(), anyLong()))
        .thenReturn(testUtil.getTenantCostTypeResponse());
    ResponseEntity<BaseResponse<TenantCostTypeResponse>> response =
        tenantCostTypeController.getTenantCostType(TestUtil.ORG_ID, TestUtil.ID);
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(200, response.getStatusCodeValue());
    verify(tenantCostTypeService, times(1)).getTenantCostType(any(), any());
  }

  @Test
  @DisplayName("Should get tenant cost type by orgId")
  void getTenantCostTypeByOrgId() throws CommonServiceException {
    TenantCostTypeResponse tenantCostTypeResponse = testUtil.getTenantCostTypeResponse();
    when(tenantCostTypeService.getTenantCostTypeByOrgId(anyString()))
        .thenReturn(List.of(tenantCostTypeResponse));
    ResponseEntity<BaseResponse<List<TenantCostTypeResponse>>> response =
        tenantCostTypeController.getTenantCostTypeByOrgId(TestUtil.ORG_ID);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(tenantCostTypeResponse, response.getBody().getPayload().get(0));
    verify(tenantCostTypeService, times(1)).getTenantCostTypeByOrgId(any());
  }

  @Test
  @DisplayName("Test updating a Tenant Cost Type")
  void updateTenantCostType() throws CommonServiceException {

    when(tenantCostTypeService.updateTenantCostType(anyLong(), anyString(), any()))
        .thenReturn(testUtil.getTenantCostTypeResponse());
    ResponseEntity<BaseResponse<TenantCostTypeResponse>> response =
        tenantCostTypeController.updateTenantCostType(
            TestUtil.ORG_ID, TestUtil.ID, testUtil.getTenantCostTypeUpdateRequest());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.DISPLAY_NAME, response.getBody().getPayload().getDisplayName());
    verify(tenantCostTypeService, times(1)).updateTenantCostType(any(), any(), any());
  }

  @Test
  void deleteTenantCostTypeTest() throws CommonServiceException {
    TenantCostTypeResponse tenantCostTypeResponse = testUtil.getTenantCostTypeResponse();
    when(tenantCostTypeService.deleteTenantCostType(anyLong(), anyString()))
        .thenReturn(tenantCostTypeResponse);

    ResponseEntity<BaseResponse<TenantCostTypeResponse>> responseEntity =
        tenantCostTypeController.deleteTenantCostType(TestUtil.ORG_ID, 1L);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(TestUtil.ID, responseEntity.getBody().getPayload().getId());

    verify(tenantCostTypeService, times(1)).deleteTenantCostType(anyLong(), anyString());
  }

  @Test
  void getTenantCostTypeCacheKeysTest() {
    List<TenantCostTypeCacheKeyDto> tenantCostTypeCacheKeyDtoList =
        testUtil.getTenantCostTypeCacheKeys();
    when(tenantCostTypeService.getAllTenantCostTypeCacheKeys(any()))
        .thenReturn(tenantCostTypeCacheKeyDtoList);
    ResponseEntity<BaseResponse<List<TenantCostTypeCacheKeyDto>>> response =
        tenantCostTypeController.getTenantCostTypeCacheKeys(2);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tenantCostTypeCacheKeyDtoList, response.getBody().getPayload());
    verify(tenantCostTypeService, times(1)).getAllTenantCostTypeCacheKeys(any());
  }
}
