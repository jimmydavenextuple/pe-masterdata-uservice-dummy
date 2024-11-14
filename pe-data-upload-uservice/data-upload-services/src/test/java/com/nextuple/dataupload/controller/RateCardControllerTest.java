/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.RateCardService;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RateCardControllerTest {

  @InjectMocks RateCardController rateCardController;
  @Mock RateCardService rateCardService;
  @InjectMocks TestUtil testUtil;

  @Test
  void getRateCardTableDataTest() {
    CostDefinitionRequest costDefinitionRequest =
        testUtil.getCostDefinitionRequest(TestUtil.SELECTOR_CF, "billWeightUps", "zones");
    CostDefinitionResponse costDefinitionResponse = testUtil.getCostDefinitionResponse();

    when(rateCardService.getRateCardTableData(anyString(), any(CostDefinitionRequest.class)))
        .thenReturn(costDefinitionResponse);

    ResponseEntity<BaseResponse<CostDefinitionResponse>> response =
        rateCardController.getRateCardTableData(TestUtil.ORG_ID, costDefinitionRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getPayload().isRateCardActive());

    verify(rateCardService, times(1))
        .getRateCardTableData(anyString(), any(CostDefinitionRequest.class));
  }
}
