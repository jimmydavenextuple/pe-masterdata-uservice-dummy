/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.sourcing.cost.config.feign.CostConfigDashboardFeign;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RateCardServiceTest {

  @InjectMocks RateCardService rateCardTableDashboardService;
  @Mock CostConfigDashboardFeign costConfigDashboardFeign;
  @InjectMocks TestUtil testUtil;

  @Test
  void getRateCardTableDataTest() {
    when(costConfigDashboardFeign.getRateCardTableData(
            anyString(), any(CostDefinitionRequest.class)))
        .thenReturn(testUtil.getFeignResponse());
    CostDefinitionResponse response =
        rateCardTableDashboardService.getRateCardTableData(
            TestUtil.ORG_ID,
            testUtil.getCostDefinitionRequest(TestUtil.SELECTOR_CF, "billWeightUps", "zones"));
    assertNotNull(response);
    assertTrue(response.isRateCardActive());
    assertEquals(2, response.getColumns().getHeaders().size());
    verify(costConfigDashboardFeign, times(1))
        .getRateCardTableData(anyString(), any(CostDefinitionRequest.class));
  }
}
