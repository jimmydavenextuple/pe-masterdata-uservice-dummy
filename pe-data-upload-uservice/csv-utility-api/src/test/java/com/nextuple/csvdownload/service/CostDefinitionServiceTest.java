/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.service.v1.CostDefinitionRequestValidationFactory;
import com.nextuple.csvdownload.service.v1.impl.CostDefinitionRequestValidationImpl;
import com.nextuple.sourcing.cost.config.dto.CostFactorHeadersInfoDto;
import com.nextuple.sourcing.cost.config.feign.CostConfigDashboardFeign;
import com.nextuple.sourcing.cost.config.feign.CostValueFeign;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import feign.FeignException;
import feign.Request;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CostDefinitionServiceTest {
  @Mock private CostConfigDashboardFeign costConfigDashboardFeign;
  @Mock private CostValueFeign costValueFeign;
  @Mock CostDefinitionRequestValidationImpl costDefinitionRequestValidation;
  @Mock private CostDefinitionRequestValidationFactory costDefinitionRequestValidationFactory;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private CostDefinitionService costDefinitionService;

  @Test
  @DisplayName("Get Cost Definition Response: Happy path")
  void getCostDefinitionResponseHappyPathTest() throws CommonServiceException {
    when(costConfigDashboardFeign.getRateCardTableData(any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getGridResponse()).build());
    when(costDefinitionRequestValidationFactory.getCostDefinitionRequestValidationFactory(any()))
        .thenReturn(costDefinitionRequestValidation);
    doNothing().when(costDefinitionRequestValidation).validateCostDefinitionRequest(any(), any());
    var response =
        costDefinitionService.getCostDefinitionResponse(
            TestUtil.ORG_ID, testUtil.getGridRequest(), testUtil.getCostTypeValidationResponse());
    assertNotNull(response);
    assertEquals(testUtil.getGridResponse(), response);
    verify(costConfigDashboardFeign, times(1)).getRateCardTableData(any(), any());
    verify(costDefinitionRequestValidationFactory, times(1))
        .getCostDefinitionRequestValidationFactory(any());
  }

  @Test
  @DisplayName("Get Cost Definition Response: Exception not cost definition found")
  void getCostDefinitionResponseExceptionTest1() throws CommonServiceException {
    when(costConfigDashboardFeign.getRateCardTableData(any(), any()))
        .thenReturn(BaseResponse.builder().build());
    when(costDefinitionRequestValidationFactory.getCostDefinitionRequestValidationFactory(any()))
        .thenReturn(costDefinitionRequestValidation);
    doNothing().when(costDefinitionRequestValidation).validateCostDefinitionRequest(any(), any());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionService.getCostDefinitionResponse(
                  TestUtil.ORG_ID,
                  testUtil.getGridRequest(),
                  testUtil.getCostTypeValidationResponse());
            });
    assertEquals("Cost definition data not found for orgId", ex.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    verify(costConfigDashboardFeign, times(1)).getRateCardTableData(any(), any());
    verify(costDefinitionRequestValidationFactory, times(1))
        .getCostDefinitionRequestValidationFactory(any());
  }

  @Test
  @DisplayName("Get Cost Definition Response: Exception while getting rate card data for orgId")
  void getCostDefinitionResponseExceptionTest2() throws CommonServiceException {
    when(costConfigDashboardFeign.getRateCardTableData(any(), any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Exception",
                Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, null, null),
                "Exception".getBytes(),
                Map.of()));
    when(costDefinitionRequestValidationFactory.getCostDefinitionRequestValidationFactory(any()))
        .thenReturn(costDefinitionRequestValidation);
    doNothing().when(costDefinitionRequestValidation).validateCostDefinitionRequest(any(), any());
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionService.getCostDefinitionResponse(
                  TestUtil.ORG_ID,
                  testUtil.getGridRequest(),
                  testUtil.getCostTypeValidationResponse());
            });
    assertTrue(ex.getMessage().contains("Exception while getting rate card data for orgId"));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
    verify(costConfigDashboardFeign, times(1)).getRateCardTableData(any(), any());
    verify(costDefinitionRequestValidationFactory, times(1))
        .getCostDefinitionRequestValidationFactory(any());
  }

  @Test
  @DisplayName("Get Cost Type validation response: Happy Path")
  void getCostTypeValidationResponseHappyPath() throws CommonServiceException {
    when(costConfigDashboardFeign.getCostTypesForValidation(any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getCostTypeValidationResponse()).build());
    CostTypeValidationResponse response =
        costDefinitionService.getCostTypeValidationResponse(
            TestUtil.ORG_ID, TestUtil.SHIPPING_COST);
    assertEquals(testUtil.getCostTypeValidationResponse(), response);
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(any(), any());
  }

  @Test
  @DisplayName(
      "Get Cost Definition Response: Exception while getting cost definition validation response")
  void getCostDefinitionResponseExceptionTest3() {
    when(costConfigDashboardFeign.getCostTypesForValidation(any(), any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Exception",
                Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, null, null),
                "Exception".getBytes(),
                Map.of()));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionService.getCostTypeValidationResponse(
                  TestUtil.ORG_ID, TestUtil.SHIPPING_COST);
            });
    assertTrue(ex.getMessage().contains("Invalid cost type for orgId"));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    assertEquals(
        testUtil.getGridRequest().getCostType(),
        ex.getFieldInfo().get("costType").getRejectedValue());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(any(), any());
  }

  private String[] getHeaderMetas(CostDefinitionResponse response) {
    return response.getColumns().getHeaders().stream()
        .map(CostFactorHeadersInfoDto::getColumnMeta)
        .toArray(String[]::new);
  }

  private String[] getHeaderNames(CostDefinitionResponse response) {
    return response.getColumns().getHeaders().stream()
        .map(CostFactorHeadersInfoDto::getColumnName)
        .toArray(String[]::new);
  }
}
