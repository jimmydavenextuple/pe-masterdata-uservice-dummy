/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_TYPE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.inbound.UpdateRateCardStatusRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.UpdateRateCardStatusResponse;
import com.nextuple.sourcing.cost.config.service.CostTypeDashboardService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CostTypeDashboardControllerTest {
  @Mock private CostTypeDashboardService costTypeDashboardService;

  @InjectMocks private CostTypeDashboardController costTypeDashboardController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Getting cost type: Happy Path")
  void getCostTypesTest() throws CommonServiceException {
    when(costTypeDashboardService.getCostTypes(any()))
        .thenReturn(testUtil.getCostTypeResponseWithSelector(ORG_ID));

    ResponseEntity<BaseResponse<CostTypeResponse>> responseEntity =
        costTypeDashboardController.getCostTypes(ORG_ID);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    verify(costTypeDashboardService, times(1)).getCostTypes(any());
  }

  @Test
  @DisplayName("Test for updating rate card status")
  void updateRateCardStatusTest() throws CommonServiceException {
    UpdateRateCardStatusRequest updateRateCardStatusRequest =
        testUtil.getUpdateRateCardStatusRequest();
    when(costTypeDashboardService.updateRateCardStatus(
            anyString(), any(UpdateRateCardStatusRequest.class)))
        .thenReturn(testUtil.getUpdateRateCardStatusResponse());

    ResponseEntity<BaseResponse<UpdateRateCardStatusResponse>> responseEntity =
        costTypeDashboardController.updateRateCardStatus(ORG_ID, updateRateCardStatusRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    verify(costTypeDashboardService, times(1))
        .updateRateCardStatus(anyString(), any(UpdateRateCardStatusRequest.class));
  }

  @Test
  @DisplayName("Getting cost type details for validation: Happy Path")
  void getCostTypesForValidation() throws CommonServiceException {
    when(costTypeDashboardService.getCostTypeDetailsForValidation(any(), any()))
        .thenReturn(testUtil.getCostTypeValidationResponse());

    ResponseEntity<BaseResponse<CostTypeValidationResponse>> responseEntity =
        costTypeDashboardController.getCostTypesForValidation(ORG_ID, COST_TYPE);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    verify(costTypeDashboardService, times(1)).getCostTypeDetailsForValidation(any(), any());
  }
}
