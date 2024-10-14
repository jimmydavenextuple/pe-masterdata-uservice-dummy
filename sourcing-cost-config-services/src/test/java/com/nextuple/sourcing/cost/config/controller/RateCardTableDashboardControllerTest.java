/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.service.RateCardTableDashboardService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RateCardTableDashboardControllerTest {

  @InjectMocks RateCardTableDashboardController rateCardTableDashboardController;
  @Mock RateCardTableDashboardService rateCardTableDashboardService;
  @InjectMocks TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getRateCardTableDataTest() throws CommonServiceException {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(TestUtil.SELECTOR_CF, "billWeightUps", "zones");
    CostDefinitionResponse costDefinitionResponse = testUtil.getCostDefinitionResponse();

    when(rateCardTableDashboardService.getRateCardTableData(
            anyString(), any(CostDefinitionRequest.class)))
        .thenReturn(costDefinitionResponse);

    ResponseEntity<BaseResponse<CostDefinitionResponse>> response =
        rateCardTableDashboardController.getRateCardTableData(
            TestUtil.ORG_ID, costDefinitionRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getPayload().isRateCardActive());

    verify(rateCardTableDashboardService, times(1))
        .getRateCardTableData(anyString(), any(CostDefinitionRequest.class));
  }
}
